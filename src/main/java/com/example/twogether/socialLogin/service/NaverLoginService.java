package com.example.twogether.socialLogin.service;

import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.common.jwt.JwtUtil;
import com.example.twogether.socialLogin.dto.NaverUserInfoDto;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j(topic = "NAVER Login")
@Service
@RequiredArgsConstructor
public class NaverLoginService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${naver.secret.key}")
    private String secretKey;
    @Value("${naver.client.id}")
    private String naverClientId;

    public String naverLogin(String code, HttpServletResponse response) throws JsonProcessingException { //String code는 네이버로부터 받은 인가 코드
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);
        // 2. 토큰으로 네이버 API 호출 : "액세스 토큰"으로 "네이버 사용자 정보" 가져오기
        NaverUserInfoDto naverUserInfo = getNaverUserInfo(accessToken);
        // 3. 필요시 회원가입 아니라면 바로 user가져오기.
        User naverUser = registerNaverUserIfNeeded(naverUserInfo);
        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(naverUser.getNickname(), naverUser.getRole());

        log.info("토큰정보"+createToken);
        jwtUtil.addJwtToCookie(createToken, response);
        return createToken;
    }


    private String getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
            .fromUriString("https://nid.naver.com")
            .path("/oauth2.0/token")
            .encode()
            .build()
            .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", naverClientId);
        body.add("client_secret", secretKey);
        body.add("code", code);
        body.add("state", "test");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
            .post(uri) // POST방식
            .headers(headers)
            .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
            requestEntity,
            String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
            .fromUriString("https://openapi.naver.com")
            .path("/v1/nid/me")
            .encode()
            .build()
            .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<Void> requestEntity = RequestEntity
            .get(uri)
            .headers(headers)
            .build();

        // HTTP 요청 보내기 // 여기서 응답을 받아옴.
        ResponseEntity<String> response = restTemplate.exchange(
            requestEntity,
            String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        String naverId = jsonNode.get("response")
            .get("id").asText();
        String nickname = jsonNode.get("response")
            .get("nickname").asText();
        String email = jsonNode.get("response")
            .get("email").asText();

        log.info("네이버 사용자 정보: " + naverId + ", " + nickname + ", " + email);
        return NaverUserInfoDto.builder().naverId(naverId).email(email).nickname(nickname).build();
    }

    private User registerNaverUserIfNeeded(NaverUserInfoDto naverUserInfo) {
        // DB 에 중복된 Naver Id 가 있는지 확인
        String naverId = naverUserInfo.getNaverId();
        User naverUser = userRepository.findByNaverId(naverId).orElse(null);

        if (naverUser == null) {
            // 네이버 사용자 email 동일한 email 가진 회원이 있는지 확인
            String naverEmail = naverUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                // 기존 회원정보에 네이버 Id 추가
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: naver email
                String email = naverUserInfo.getEmail();

                naverUser = User.builder()
                    .nickname(naverUserInfo.getNickname())
                    .password(encodedPassword)
                    .email(email)
                    .role(UserRoleEnum.USER)
                    .naverId(naverId)
                    .build();
            }

            userRepository.save(naverUser);
        }
        return naverUser;
    }

}

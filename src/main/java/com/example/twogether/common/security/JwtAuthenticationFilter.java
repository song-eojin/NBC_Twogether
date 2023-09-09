package com.example.twogether.common.security;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.jwt.JwtUtil;
import com.example.twogether.common.redis.RedisRefreshToken;
import com.example.twogether.user.dto.LoginRequestDto;
import com.example.twogether.user.entity.UserRoleEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final RedisRefreshToken redisRefreshToken;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisRefreshToken redisRefreshToken) {
        this.jwtUtil = jwtUtil;
        this.redisRefreshToken = redisRefreshToken;
        setFilterProcessesUrl("/api/users/login");
    }

    private final String contentType = "application/json";

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(),
                LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    requestDto.getEmail(),
                    requestDto.getPassword(),
                    null
                )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        String email = ((UserDetailsImpl) authResult.getPrincipal()).getEmail();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();
        String memberId = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getId().toString();

        String accessToken = jwtUtil.createToken(email, role);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);

        String refreshToken = jwtUtil.createRefreshToken();
        response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, refreshToken);
        redisRefreshToken.saveRefreshToken(refreshToken, memberId);

        response.setStatus(200);
        response.setContentType(contentType);
        String result = new ObjectMapper().writeValueAsString(
            new ApiResponseDto(HttpStatus.OK.value(), "Login Success"));

        response.getOutputStream().print(result);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        log.info("로그인 실패");

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType(contentType);
        String result = new ObjectMapper().writeValueAsString(
            new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), "Login Failed"));

        response.getOutputStream().print(result);
    }
}

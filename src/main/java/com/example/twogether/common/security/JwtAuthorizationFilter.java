package com.example.twogether.common.security;

import com.example.twogether.common.dto.ErrorResponseDto;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.jwt.JwtUtil;
import com.example.twogether.common.redis.RedisRefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisRefreshToken redisRefreshToken;
    private final UserDetailsServiceImpl userDetailsService;

    private final String contentType = "application/json";

    @Override
    protected void doFilterInternal(
        HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
        throws ServletException, IOException {
        // Header에서 jwt 토큰 받아오기
        String accessToken = jwtUtil.getTokenFromRequest(req);
        String refreshToken = req.getHeader(JwtUtil.REFRESH_TOKEN_HEADER);

        if (StringUtils.hasText(accessToken)) {
            boolean isValid = false;

            try {
                isValid = jwtUtil.validateToken(accessToken);
            } catch (ExpiredJwtException e) {
                log.error("액세스 토큰 만료됨 : " + accessToken);
                if (!redisRefreshToken.hasKey(refreshToken)) {
                    printAuthenticationFailure(res, true);
                    return;
                }

                isValid = true;
                Long memberId = Long.parseLong(redisRefreshToken.getMemberId(refreshToken));
                UserDetailsImpl userDetails = userDetailsService.loadUserById(memberId);

                redisRefreshToken.removeRefreshToken(refreshToken);
                accessToken = jwtUtil.createToken(userDetails.getEmail(),
                    userDetails.getUser().getRole());
                refreshToken = jwtUtil.createRefreshToken();

                redisRefreshToken.saveRefreshToken(refreshToken, memberId.toString());
                res.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
                res.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, refreshToken);
            }

            if (!isValid) {
                printAuthenticationFailure(res, false);
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(accessToken);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                // 인증 처리에 실패한 경우 처리
                log.error(e.getMessage());
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                res.setContentType(contentType);
                String result = new ObjectMapper().writeValueAsString(
                    new ErrorResponseDto(CustomErrorCode.USER_NOT_FOUND));

                res.getOutputStream().print(result);
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }

    // 인증 실패 메시지 처리
    private void printAuthenticationFailure(HttpServletResponse res, boolean isAccessToken)
        throws IOException {
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.setContentType(contentType);
        String result = new ObjectMapper().writeValueAsString(
            new ErrorResponseDto(isAccessToken ?
                CustomErrorCode.ACCESS_TOKEN_INVALID : CustomErrorCode.ALL_TOKENS_EXPIRED));

        res.getOutputStream().print(result);
    }
}

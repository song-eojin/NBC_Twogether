package com.example.twogether.socialLogin.controller;

import com.example.twogether.common.jwt.JwtUtil;
import com.example.twogether.socialLogin.service.KakaoLoginService;
import com.example.twogether.socialLogin.service.NaverLoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class SocialLonginController {

    private final KakaoLoginService kakaoLoginService;
    private final NaverLoginService naverLoginService;

    @GetMapping("/social/kakao/callback") //버튼을 누르게 되면 카카오 서버로부터 리다이렉트되어 인가 코드를 전달받게됨. 해당 URL은 카카오 로그인 홈페이지에서 등록해뒀음.
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        String token = kakaoLoginService.kakaoLogin(code, response);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token); //check 토큰은 헤더에 넣어서 전달하는 방식.

        return "redirect:/views/workspace";
    }

    @GetMapping("/social/naver/callback") //버튼을 누르게 되면 네이버 서버로부터 리다이렉트되어 인가 코드를 전달받게됨. 해당 URL은 네이버 로그인 홈페이지에서 등록해뒀음.
    public String naverLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        String token = naverLoginService.naverLogin(code, response);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token); //check 토큰은 헤더에 넣어서 전달하는 방식.

        return "redirect:/views/workspace";
    }

}

package com.example.twogether.mail.controller;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.mail.dto.EmailCerificationRequstDto;
import com.example.twogether.mail.service.EmailCerificationService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class EmailCerificationController {

    private final EmailCerificationService emailCerificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponseDto> sendVerificationNumber(
        @Validated @RequestBody EmailCerificationRequstDto request)
        throws MessagingException, NoSuchAlgorithmException {
        try {
            emailCerificationService.sendEmailForCertification(request.getEmail());
        } catch (UnsupportedEncodingException e) {
            throw new CustomException(CustomErrorCode.EMAIL_SEND_FAILED);
        }

        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "인증번호 발송"));
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponseDto> verifyCertificationNumber(
        @RequestParam("certificationNumber") String certificationNumber,
        @RequestParam("email") String email) {
        emailCerificationService.verifyEmail(certificationNumber, email);

        return ResponseEntity.ok()
            .body(new ApiResponseDto(HttpStatus.OK.value(), "인증 완료"));
    }
}

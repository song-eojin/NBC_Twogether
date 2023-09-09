package com.example.twogether.mail.service;

import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.common.redis.RedisEmail;
import com.example.twogether.user.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailCerificationService {

    private final UserRepository userRepository;
    private final JavaMailSender emailSender;
    private final RedisEmail redisUtil;

    // 인증 요청 메일 전송
    @Transactional
    public void sendEmailForCertification(String email)
        throws NoSuchAlgorithmException, MessagingException, UnsupportedEncodingException {
        // 기존에 이미 사용 중인 이메일인지 확인
        findExistingUserByEmail(email);

        String certificationNumber = getVertificationNumber();
        sendMail(email, certificationNumber);

        if (redisUtil.hasKey(email)) {
            redisUtil.removeCertificationNumber(email);
        }
        redisUtil.saveCertification(email, certificationNumber);
    }

    // 인증 번호 확인
    @Transactional
    public void verifyEmail(String certificationNumber, String email) {
        verifyCertification(certificationNumber, email);
        redisUtil.updateVerified(email);
    }

    private void findExistingUserByEmail(String email) {
        if (userRepository.findByEmail(email).orElse(null) != null) {
            throw new CustomException(CustomErrorCode.EMAIL_ALREADY_USED);
        }
    }

    // 인증번호 생성기
    private static String getVertificationNumber() throws NoSuchAlgorithmException {
        String result;

        do {
            int i = SecureRandom.getInstanceStrong().nextInt(999999);
            result = String.valueOf(i);
        } while (result.length() != 6);

        return result;
    }

    private void sendMail(String to, String password)
        throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("Twogether 회원가입 이메일 인증");

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<h1> Twogether 입니다</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += password + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("kheeyeoul@gmail.com", "Twogether"));

        emailSender.send(message);
    }

    private void verifyCertification(String certificationNumber, String email) {
        if (!redisUtil.hasKey(email)) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_FOUND);
        }

        if (!Objects.equals(redisUtil.getCertificationNumber(email), certificationNumber)) {
            throw new CustomException(CustomErrorCode.INVALID_CERTIFICATION_NUMBER);
        }
    }
}

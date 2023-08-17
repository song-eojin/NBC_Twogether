package com.example.twogether.user.service;

import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.user.dto.SignupRequestDto;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String adminToken;

    @Transactional
    public User signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 존재 여부 확인
        findExistingUserByEmail(email);

        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin() && requestDto.getAdminToken().equals(adminToken)) {
            role = UserRoleEnum.ADMIN;
        }

        return userRepository.save(requestDto.toEntity(password, role));
    }

    private void findExistingUserByEmail(String email) {
        if(userRepository.findByEmail(email).orElse(null) != null)
            throw new CustomException(CustomErrorCode.USER_ALREADY_EXISTS);
    }
}
package com.example.twogether.user.dto;

import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private String introduction;
    private String icon;
    private UserRoleEnum role;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .introduction(user.getIntroduction())
            .nickname(user.getNickname())
            .role(user.getRole())
            .icon(user.getIcon())
            .build();
    }
}
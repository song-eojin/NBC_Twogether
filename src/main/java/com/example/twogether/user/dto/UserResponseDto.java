package com.example.twogether.user.dto;

import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private UserRoleEnum role;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder().id(user.getId()).email(user.getEmail())
            .nickname(user.getNickname()).role(user.getRole()).build();
    }
}
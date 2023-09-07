package com.example.twogether.user.dto;

import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserRoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"
        , message = "이메일 정규식에 어긋나는 입력입니다.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
        , message = "숫자/소문자/특수문자를 각각 1자씩 포함한 8자 이상으로 비밀번호를 구성해야 합니다.")
    private String password;

    private boolean admin;
    private String adminToken;

    public User toEntity(String password, UserRoleEnum role) {
        String nickname = email.substring(0, email.indexOf("@"));
        return User.builder()
            .email(this.email).password(password).role(role)
            .nickname(nickname).introduction("Hi! My name is " + nickname + " :D").build();
    }
}

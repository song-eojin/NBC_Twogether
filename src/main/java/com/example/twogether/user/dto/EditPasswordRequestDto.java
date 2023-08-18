package com.example.twogether.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class EditPasswordRequestDto {
    private String password;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
        , message = "숫자/소문자/특수문자를 각각 1자씩 포함한 8자 이상으로 비밀번호를 구성해야 합니다.")
    private String newPassword;

}

package com.example.twogether.mail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class EmailCerificationRequstDto {

    @NotBlank
    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"
        , message = "이메일 정규식에 어긋나는 입력입니다.")
    private String email;
}

package com.example.twogether.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditUserRequestDto {
    private String nickname;
    private String introduction;
}

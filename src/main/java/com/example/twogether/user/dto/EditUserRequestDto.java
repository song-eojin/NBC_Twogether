package com.example.twogether.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class EditUserRequestDto {
    private String nickname;
    private String introduction;
}

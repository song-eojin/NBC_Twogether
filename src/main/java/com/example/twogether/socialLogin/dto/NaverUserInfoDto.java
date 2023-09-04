package com.example.twogether.socialLogin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class NaverUserInfoDto {
    private String naverId;
    private String nickname;
    private String email;
}

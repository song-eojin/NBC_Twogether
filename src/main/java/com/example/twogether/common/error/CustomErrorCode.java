package com.example.twogether.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomErrorCode {
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 사용자입니다.");

    private final int errorCode;
    private final String errorMessage;

    CustomErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}

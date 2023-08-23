package com.example.twogether.common.dto;

import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponseDto {
    private final int status;
    private final String code;
    private final String message;

    public ErrorResponseDto(CustomErrorCode errorCode) {
        this.status = HttpStatus.BAD_REQUEST.value();
        this.code = errorCode.getCode();
        this.message = errorCode.getErrorMessage();
    }

    public static ResponseEntity<ErrorResponseDto> error(CustomException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(e.getErrorCode().getCode())
                .message(e.getErrorCode().getErrorMessage())
                .build());
    }
}

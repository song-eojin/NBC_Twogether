package com.example.twogether.common.exception;

import com.example.twogether.common.dto.ApiResponseDto;
import com.example.twogether.common.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorResponseDto> handlerCustomException(CustomException e) {
        log.error("[CustomException] {} : {}",e.getErrorCode().getCode(), e.getErrorCode().getErrorMessage());
        return ErrorResponseDto.error(e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponseDto> handlerValidationException(
        MethodArgumentNotValidException ex) {

        // Validation 예외처리
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : ex.getFieldErrors()) {
            log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            errorMessage.append(fieldError.getField()).append(" 필드 : ")
                .append(fieldError.getDefaultMessage()).append(" ");
        }

        return ResponseEntity.badRequest()
            .body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), errorMessage.toString()));
    }
}
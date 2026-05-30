package com.example.runsense.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 부모인 RuntimeException에도 메시지 주입
        this.errorCode = errorCode;
    }
}
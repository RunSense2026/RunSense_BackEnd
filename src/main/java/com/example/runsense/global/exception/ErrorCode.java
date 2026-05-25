package com.example.runsense.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // 회원가입
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 등록된 이메일 주소입니다."),

    // 유효성 검증 실패
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다.");

    private final HttpStatus status;
    private final String message;
}

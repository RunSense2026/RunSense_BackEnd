package com.example.runsense.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // 회원가입
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 등록된 이메일 주소입니다."),

    // 로그인 / 인증
    INVALID_LOGIN_INFO(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "재발급 토큰을 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // 러닝 액티비티 조회
    RUNNING_ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "러닝 액티비티를 찾을 수 없습니다."),

    // 유효성 검증 실패
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    // AI 코칭
    AI_SERVICE_ERROR(HttpStatus.BAD_GATEWAY, "AI 서버와 통신 중 오류가 발생했습니다."),

    // 기본 예외
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}

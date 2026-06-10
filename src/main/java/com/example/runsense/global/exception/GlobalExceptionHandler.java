package com.example.runsense.global.exception;

import com.example.runsense.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 애플리케이션 전역에서 발생하는 예외를 공통 응답 포맷으로 처리하는 클래스
 */
@RestControllerAdvice // JSON 형태로 예외 응답을 반환하기 위해 사용함. 이 어노테이션이 있어서 메소드에서 @ExceptionHandler로 작성한 예외 발생시 자동으로 호출
public class GlobalExceptionHandler {

    /**
     * 비즈니스 로직 검증 실패시 발생하는 IllegalArgumentException을 처리
     * @param e 발생한 예외 객체
     * @return ApiResponse 이용한 에러 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        // HTTP 상태 코드를 직접 지정하여 반환
        return new ResponseEntity<>(
                ApiResponse.fail(errorCode.getMessage()), // status 필드 제거된 fail 호출
                errorCode.getStatus()
        );
    }

    /**
     * Valid 유효성 검증 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        return new ResponseEntity<>(
                ApiResponse.fail(errorCode.getMessage(), errors), // 메시지와 에러 상세만 전달
                errorCode.getStatus() // 400 Bad Request가 자동으로 설정됨
        );
    }
}
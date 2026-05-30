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
    public ApiResponse<Void> handleIllegalArgumentException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        //errorCode.getStatus()가 HttpStatus 객체이므로, .value()로 오류 코드 꺼내기
        return ApiResponse.fail(errorCode.getStatus().value(), errorCode.getMessage());
    }

    /**
     * Valid 유효성 검증 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        // 에러 난 필드 확인
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE; // 앞서 만든 에러코드 사용

        // ApiResponse.fail(상태코드, 메시지, 에러맵) 바인딩
        ApiResponse<Map<String, String>> response = ApiResponse.fail(
                errorCode.getStatus().value(), // 400
                errorCode.getMessage(),        // "입력값이 올바르지 않습니다."
                errors                         // { "email": "올바르지 않은 이메일 형식입니다." }
        );

        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}
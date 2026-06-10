package com.example.runsense.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 모든 API 응답의 공통 형식 정의하는 클래스
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    /**
     * API 요청이 성공했을 때 200 상태 코드와 함께 데이터를 담아 반환하는 편의 메서드
     * @param data 클라이언트에게 전달할 실제 데이터 객체
     * @param <T>  반환할 데이터의 타입
     * @return 성공 상태(200)와 메시지, 데이터가 포함된 ApiResponse 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "성공", data);
    }

    /**
     * API 요청이 실패했을 때 지정된 에러 코드(param)와 메시지를 담아 반환하는 편의 메서드
     * 주로 GlobalExceptionHandler에서 전역 예외 처리 시 에러 응답을 생성할 때 사용!!!!!
     *
     * @param message 구체적인 에러 메시지
     * @param <T>  실패 응답이므로 내부 데이터는 null로 바인딩됨을 명시하는 제네릭 타입
     * @return 지정된 에러 정보가 담긴 ApiResponse 객체
     */
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * 추가: 에러 상세 데이터(예: 유효성 검증 실패 목록)를 함께 담아 반환하는 fail 메서드
     */
    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}

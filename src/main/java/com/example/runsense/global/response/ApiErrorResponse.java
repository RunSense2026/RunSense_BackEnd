package com.example.runsense.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Swagger 문서용 공통 에러 응답 스키마.
 * 실제 런타임 응답은 {@link ApiResponse#fail}이 그대로 사용되며, 이 클래스는
 * 이름이 없는(anonymous) Schema가 OpenAPI 스펙에 중복 생성되는 것을 막기 위해
 * 모든 에러 응답이 참조(ref)하는 고정된 이름의 스키마를 제공하는 용도로만 쓰인다.
 */
@Getter
@AllArgsConstructor
@Schema(name = "ApiErrorResponse", description = "공통 에러 응답 DTO")
public class ApiErrorResponse {

    @Schema(description = "성공 여부", example = "false")
    private boolean success;

    @Schema(description = "에러 메시지", example = "서버 오류가 발생했습니다.")
    private String message;

    @Schema(description = "에러 상세 데이터 (유효성 검증 실패 시 필드별 메시지 등, 없으면 null)", nullable = true)
    private Object data;
}

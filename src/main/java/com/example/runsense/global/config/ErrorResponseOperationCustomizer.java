package com.example.runsense.global.config;

import com.example.runsense.global.exception.ApiErrorAnnotation;
import com.example.runsense.global.exception.ErrorCode;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

public class ErrorResponseOperationCustomizer implements OperationCustomizer {

    /**
     * 모든 에러 응답이 공유하는 참조용 스키마 이름.
     * {@link OpenApiConfig}에서 이 이름으로 실제 스키마를 components에 등록한다.
     */
    public static final String ERROR_SCHEMA_NAME = "ApiErrorResponse";
    private static final String ERROR_SCHEMA_REF = "#/components/schemas/" + ERROR_SCHEMA_NAME;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // 공통 500 에러 추가
        addResponse(operation, new ApiResponse(), ErrorCode.INTERNAL_SERVER_ERROR);

        // 메서드에 정의된 에러 추가
        ApiErrorAnnotation annotation = handlerMethod.getMethodAnnotation(ApiErrorAnnotation.class);
        if (annotation != null) {
            for (ErrorCode errorCode : annotation.value()) {
                addResponse(operation, new ApiResponse(),  errorCode);
            }
        }
        return operation;
    }

    private void addResponse(Operation operation, ApiResponse operationResponse, ErrorCode errorCode) {
        // 응답 예시 객체 생성
        var failResponse = com.example.runsense.global.response.ApiResponse.fail(errorCode.getMessage());

        // 스웨거 미디어 타입 구성: 이름 없는 익명 스키마 대신 공통 에러 스키마를 참조(ref)하고,
        // 예시 값은 schema가 아닌 example 필드에 별도로 붙인다 ($ref와 example을 같은 레벨에 둬도 유효함)
        MediaType mediaType = new MediaType()
                .schema(new Schema<>().$ref(ERROR_SCHEMA_REF))
                .example(failResponse);

        // 콘텐츠 구성
        Content content = new Content().addMediaType("application/json", mediaType);

        // 최종 ApiResponse 구성
        operationResponse.description(errorCode.getMessage())
                .content(content);;

        operation.getResponses().addApiResponse(String.valueOf(errorCode.getStatus().value()), operationResponse);
    }

}

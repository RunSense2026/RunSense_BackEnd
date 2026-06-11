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

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // 공통 500 에러 추가
        addResponse(operation, ErrorCode.INTERNAL_SERVER_ERROR);

        // 메서드에 정의된 에러 추가
        ApiErrorAnnotation annotation = handlerMethod.getMethodAnnotation(ApiErrorAnnotation.class);
        if (annotation != null) {
            for (ErrorCode errorCode : annotation.value()) {
                addResponse(operation, errorCode);
            }
        }
        return operation;
    }

    private void addResponse(Operation operation, ErrorCode errorCode) {
        String exampleJson = String.format(
                "{\"success\": false, \"message\": \"%s\", \"data\": null}",
                errorCode.getMessage()
        );

        ApiResponse apiResponse = new ApiResponse()
                .description(errorCode.getMessage())
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>()
                                .example(exampleJson))));

        operation.getResponses().addApiResponse(String.valueOf(errorCode.getStatus().value()), apiResponse);
    }

}

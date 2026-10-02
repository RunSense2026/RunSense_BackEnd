package com.example.runsense.global.config;

import com.example.runsense.global.response.ApiErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정 (JWT Bearer 토큰 기반 인증)
 */
@Configuration
public class OpenApiConfig {

	/**
	 * Swagger UI에 JWT 인증 스키마 추가
	 * Authorize 버튼 활성화 및 API 요청 시 Authorization 헤더 자동 추가
	 */
	@Bean
	public OpenAPI openAPI() {
		// HTTP Bearer 인증 스키마 정의 (JWT 토큰 입력 받기 위함)
		SecurityScheme securityScheme = new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT")
				// Swagger UI의 Authorize 창에 표시될 설명
				.description("JWT 토큰을 입력하세요. 예: eyJhbGc...");

		// Swagger 요청에 BearerAuth 보안 스키마 적용
		SecurityRequirement securityRequirement = new SecurityRequirement()
				.addList("BearerAuth");

		return new OpenAPI()
				.info(new Info()
						.title("RunSense API")
						.version("1.0.0")
						.description("JWT 기반 사용자 인증 API"))
				// 모든 API에 보안 요구사항 추가 (Authorize 버튼 필수화)
				.addSecurityItem(securityRequirement)
				// BearerAuth 보안 스키마 정의
				.components(new Components()
						.addSecuritySchemes("BearerAuth", securityScheme));
	}

	/**
	 * Swagger에 오류 응답을 보여주기 위함
	 *
	 */
	@Bean
	public ErrorResponseOperationCustomizer errorResponseOperationCustomizer() {
		return new ErrorResponseOperationCustomizer();
	}

	/**
	 * ErrorResponseOperationCustomizer가 모든 에러 응답에서 $ref로 참조하는
	 * 공통 에러 스키마("ApiErrorResponse")를 components.schemas에 등록한다.
	 * 이게 없으면 각 에러 응답이 이름 없는 익명 스키마로 중복 생성되어,
	 * 프론트 OpenAPI 코드젠이 S_2, S_16 같은 의미 없는 placeholder 타입을 만들어낸다.
	 */
	@Bean
	public GlobalOpenApiCustomizer errorSchemaOpenApiCustomizer() {
		return openApi -> {
			ResolvedSchema resolvedSchema = ModelConverters.getInstance()
					.resolveAsResolvedSchema(new AnnotatedType(ApiErrorResponse.class));

			openApi.getComponents().addSchemas(ErrorResponseOperationCustomizer.ERROR_SCHEMA_NAME, resolvedSchema.schema);
			resolvedSchema.referencedSchemas.forEach(openApi.getComponents()::addSchemas);
		};
	}
}

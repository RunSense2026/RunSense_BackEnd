package com.example.runsense.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "인증 토큰 응답 DTO")
public class UserTokenResponseDto {
	@Schema(description = "토큰 타입", example = "Bearer")
	private String tokenType;
	
	@Schema(description = "API 호출 시 사용하는 액세스 토큰 (1시간 유효)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;
	
	@Schema(description = "액세스 토큰 재발급 시 사용하는 리프레시 토큰 (14일 유효, 서버에 저장됨)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String refreshToken;
	
	@Schema(description = "액세스 토큰 만료 시간 (초 단위)", example = "3600")
	private long expiresIn;

	public static UserTokenResponseDto of(String accessToken, String refreshToken, long expiresIn) {
		return UserTokenResponseDto.builder()
				.tokenType("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.expiresIn(expiresIn)
				.build();
	}
}
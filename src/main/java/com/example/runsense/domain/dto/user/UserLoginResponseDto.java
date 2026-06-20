package com.example.runsense.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "인증 토큰 응답 DTO")
public class UserLoginResponseDto {
	@Schema(description = "유저 식별 Id", example = "1")
	private long userID;

	@Schema(description = "유저 이름", example = "홍길동")
	private String userName;

	@Schema(description = "이메일", example = "user@example.com")
	private String email;

	// === 추가된 필드들 ===
	@Schema(description = "가입 날짜", example = "2026-06-20")
	private LocalDate createdAt;

	@Schema(description = "총 러닝 거리 (km)", example = "120.5")
	private double totalDistance;

	@Schema(description = "총 러닝 횟수", example = "24")
	private int totalCount;
	// ====================

	@Schema(description = "토큰 타입", example = "Bearer")
	private String tokenType;
	
	@Schema(description = "API 호출 시 사용하는 액세스 토큰 (1시간 유효)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String accessToken;
	
	@Schema(description = "액세스 토큰 재발급 시 사용하는 리프레시 토큰 (14일 유효, 서버에 저장됨)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	private String refreshToken;
	
	@Schema(description = "액세스 토큰 만료 시간 (초 단위)", example = "3600")
	private long expiresIn;

	public static UserLoginResponseDto of(Long userId, String userName, String email, LocalDate createdAt, double totalDistance, int totalCount, String accessToken, String refreshToken, long expiresIn) {
		return UserLoginResponseDto.builder()
				.userID(userId)
				.userName(userName)
				.email(email)
				.createdAt(createdAt)
				.totalDistance(totalDistance)
				.totalCount(totalCount)
				.tokenType("Bearer")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.expiresIn(expiresIn)
				.build();
	}
}
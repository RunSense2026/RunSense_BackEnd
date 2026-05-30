package com.example.runsense.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "토큰 로그아웃 요청 DTO")
public class TokenLogoutRequestDto {
	@Schema(description = "로그아웃할 리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
	@NotBlank(message = "refreshToken은 필수 입력 값입니다.")
	private String refreshToken;
}
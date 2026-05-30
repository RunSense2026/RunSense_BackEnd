package com.example.runsense.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "이메일 중복 확인 응답 DTO")
public class EmailDuplicateCheckResponseDto {
	@Schema(description = "중복 여부", example = "true")
	private Boolean isDuplicated;
}
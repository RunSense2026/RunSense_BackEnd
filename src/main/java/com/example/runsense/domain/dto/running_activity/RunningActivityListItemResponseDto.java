package com.example.runsense.domain.dto.running_activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "러닝 액티비티 목록 항목 응답 DTO")
public class RunningActivityListItemResponseDto {

	@Schema(description = "러닝 액티비티 ID", example = "123")
	private Long activityId;

	@Schema(description = "사용자 ID", example = "42")
	private Long userId;

	@Schema(description = "거리(km)", example = "5.3")
	private Double distance;

	@Schema(description = "총 시간(형식 HH:mm:ss)", example = "00:25:12")
	private String time;

	@Schema(description = "평균 페이스(분:초)", example = "05:00")
	private String pace;

	@Schema(description = "운동 일시(ISO-8601)", example = "2026-05-27T07:30:00")
	private LocalDateTime activityDate;

	@Schema(description = "폼 점수", example = "8")
	private Short formScore;
}
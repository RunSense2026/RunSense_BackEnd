package com.example.runsense.domain.dto.running_activity.detail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "러닝 액티비티 상세 조회 응답 DTO")
public class RunningActivityDetailResponseDto {

	@Schema(description = "러닝 액티비티 ID", example = "1")
	private Long activityId;

	@Schema(description = "구간 기록 목록")
	private List<LapSplitReadResponseDto> lapSplits;

	@Schema(description = "발 센서 데이터 목록")
	private List<FootSensorDataReadResponseDto> footSensorData;

	@Schema(description = "AI 코칭 목록")
	private List<AiCoachingReadResponseDto> aiCoachings;
}
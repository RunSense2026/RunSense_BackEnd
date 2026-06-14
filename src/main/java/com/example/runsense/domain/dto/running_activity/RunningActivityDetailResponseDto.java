package com.example.runsense.domain.dto.running_activity;

import com.example.runsense.domain.dto.running_activity.sub.AiCoachingResponseDto;
import com.example.runsense.domain.dto.running_activity.sub.FootSensorDataResponseDto;
import com.example.runsense.domain.dto.running_activity.sub.LapSplitResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "러닝 액티비티 상세 조회 응답 DTO")
public class RunningActivityDetailResponseDto {

    @Schema(description = "러닝 액티비티 ID", example = "1")
    private Long activityId;

    @Schema(description = "거리", example = "5.00")
    private Double distance;

    @Schema(description = "시간", example = "00:30:00")
    private String time;

    @Schema(description = "페이스", example = "5'00")
    private String pace;

    @Schema(description = "생성 날짜", example = "1")
    private LocalDateTime activityDate;

    @Schema(description = "폼 점수", example = "1")
    private Short formScore;

    @Schema(description = "구간 기록 목록")
    private List<LapSplitResponseDto> lapSplits;

    @Schema(description = "발 센서 데이터 목록")
    private List<FootSensorDataResponseDto> footSensorData;
}
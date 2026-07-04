package com.example.runsense.domain.dto.ai.sub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "요약 리포트 요청용 코칭 로그 DTO")
public class CoachingLogDto {

    @Schema(description = "피드백 내용", example = "왼발 압력을 줄이고 양발 균형을 맞춰보세요.")
    @JsonProperty("feedback_text")
    private String feedbackText;

    @Schema(description = "분석 중점", example = "왼발 하중 편중 개선")
    @JsonProperty("focus_point")
    private String focusPoint;

    @Schema(description = "추천 운동", example = "오른발 지지 안정화와 좌우 밸런스 훈련")
    @JsonProperty("rec_exercise")
    private String recExercise;
}

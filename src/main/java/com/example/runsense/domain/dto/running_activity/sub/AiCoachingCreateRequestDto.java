package com.example.runsense.domain.dto.running_activity.sub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "AI 코칭 요약 요청 DTO")
public class AiCoachingCreateRequestDto {

    @Schema(description = "피드백 내용", example = "호흡을 가다듬고 페이스를 조금 낮춰보세요.")
    private String feedbackText;

    @Schema(description = "분석 중점", example = "호흡 안정과 페이스 조절")
    private String focusPoint;

    @Schema(description = "추천 운동", example = "가벼운 조깅과 호흡 조절 훈련")
    private String recExercise;
}
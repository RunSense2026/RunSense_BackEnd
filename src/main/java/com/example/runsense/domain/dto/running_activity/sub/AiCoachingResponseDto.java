package com.example.runsense.domain.dto.running_activity.sub;

import com.example.runsense.domain.entity.AiCoaching;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AI 코칭 조회 응답 DTO")
public class AiCoachingResponseDto {

    @Schema(description = "코칭 ID", example = "1")
    private Long coachingId;

    @Schema(description = "피드백 내용", example = "호흡을 가다듬고 페이스를 조금 낮춰보세요.")
    private String feedbackText;

    @Schema(description = "분석 중점", example = "호흡 안정과 페이스 조절")
    private String focusPoint;

    @Schema(description = "추천 운동", example = "가벼운 조깅과 호흡 조절 훈련")
    private String recExercise;

    public static AiCoachingResponseDto from(AiCoaching aiCoaching) {
        return AiCoachingResponseDto.builder()
                .coachingId(aiCoaching.getCoachingId())
                .feedbackText(aiCoaching.getFeedbackText())
                .focusPoint(aiCoaching.getFocusPoint())
                .recExercise(aiCoaching.getRecExercise())
                .build();
    }
}
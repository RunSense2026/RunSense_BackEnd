package com.example.runsense.domain.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "AI 코칭 생성 응답 DTO")
public class CoachingResponseDto {

    @Schema(description = "피드백 내용", example = "호흡을 안정시키고 페이스를 낮춰보세요.")
    @JsonProperty("feedback_text")
    private String feedbackText;

    @Schema(description = "분석 중점", example = "호흡 안정과 페이스 조절")
    @JsonProperty("focus_point")
    private String focusPoint;

    @Schema(description = "추천 운동", example = "가벼운 조깅과 호흡 조절 훈련")
    @JsonProperty("rec_exercise")
    private String recExercise;

    @Schema(description = "음성 안내 파일 경로", example = "/audio/coach_xxxx.mp3")
    @JsonProperty("voice_file")
    private String voiceFile;
}

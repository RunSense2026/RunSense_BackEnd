package com.example.runsense.domain.dto.ai.sub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "요약 리포트 항목 DTO")
public class ReportItemDto {

    @Schema(description = "항목 제목", example = "왼발 하중 편중 개선")
    private String title;

    @Schema(description = "항목 설명", example = "왼발 압력을 줄이고 양발 균형을 맞춰보세요.")
    private String description;

    @Schema(description = "추천 운동", example = "오른발 지지 안정화와 좌우 밸런스 훈련")
    @JsonProperty("rec_exercise")
    private String recExercise;

    @Schema(description = "해당 항목 발생 횟수", example = "2")
    private Integer count;
}

package com.example.runsense.domain.dto.ai;

import com.example.runsense.domain.dto.ai.sub.FootDataDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "AI 코칭 생성 요청 DTO")
public class CoachingRequestDto {
    @Schema(description = "페이스. null이면 AI 서버에 null로 그대로 전달", example = "5.3")
    private Double pace;

    @Schema(description = "심박수. null이면 AI 서버에 null로 그대로 전달", example = "180")
    @JsonProperty("heart_rate")
    private Integer heartRate;

    @Schema(description = "케이던스. null이면 AI 서버에 null로 그대로 전달", example = "150")
    private Integer cadence;

    @Schema(description = "왼발 데이터. null이면 AI 서버에 null로 그대로 전달")
    @JsonProperty("left_foot")
    private FootDataDto leftFoot;

    @Schema(description = "오른발 데이터. null이면 AI 서버에 null로 그대로 전달")
    @JsonProperty("right_foot")
    private FootDataDto rightFoot;
}

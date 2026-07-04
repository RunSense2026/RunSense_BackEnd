package com.example.runsense.domain.dto.ai.sub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "발 압력 히트맵 DTO")
public class HitmapDto {

    @Schema(description = "앞발바닥 왼쪽 압력", example = "20")
    @JsonProperty("forefoot_left")
    private Integer forefootLeft;

    @Schema(description = "앞발바닥 오른쪽 압력", example = "15")
    @JsonProperty("forefoot_right")
    private Integer forefootRight;

    @Schema(description = "중족부 왼쪽 압력", example = "10")
    @JsonProperty("midfoot_left")
    private Integer midfootLeft;

    @Schema(description = "중족부 오른쪽 압력", example = "8")
    @JsonProperty("midfoot_right")
    private Integer midfootRight;

    @Schema(description = "뒤꿈치 압력", example = "70")
    private Integer heel;
}

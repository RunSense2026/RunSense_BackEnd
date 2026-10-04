package com.example.runsense.domain.dto.ai.sub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "AI 코칭 요청용 발 데이터 DTO")
public class FootDataDto {

    @Schema(description = "회내유형. null이면 AI 서버에 null로 그대로 전달", example = "overpronation")
    @JsonProperty("pronation_type")
    private String pronationType;

    @Schema(description = "뒤꿈치 착지율. null이면 AI 서버에 null로 그대로 전달", example = "85")
    @JsonProperty("heel_strike_rate")
    private Integer heelStrikeRate;

    @Schema(description = "발 압력 히트맵 데이터. null이면 AI 서버에 null로 그대로 전달")
    private HitmapDto hitmap;
}

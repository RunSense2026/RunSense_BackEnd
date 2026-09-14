package com.example.runsense.domain.dto.ai.sub;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "AI 코칭 요청용 유저 프로필 DTO")
public class ProfileDto {

    @Schema(description = "나이", example = "24")
    private Integer age;

    @Schema(description = "키(cm)", example = "170")
    private Double height;

    @Schema(description = "몸무게(kg)", example = "70")
    private Double weight;

    @Schema(description = "안정시 심박수", example = "58")
    @JsonProperty("resting_heart_rate")
    private Double restingHeartRate;

    @Schema(description = "젖산역치 심박수(LTHR)", example = "162")
    private Integer lthr;
}

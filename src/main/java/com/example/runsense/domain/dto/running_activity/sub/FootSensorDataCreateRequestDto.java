package com.example.runsense.domain.dto.running_activity.sub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "발 센서 데이터 요청 DTO")
public class FootSensorDataCreateRequestDto {

    @Schema(description = "발 방향", example = "Left")
    private String footSide;

    @Schema(description = "회내유형", example = "과내선")
    private String pronationType;

    @Schema(description = "뒤꿈치 착지율", example = "99.0")
    private Double heelStrikeRate;

    @Schema(description = "히트맵 데이터", example = "{}")
    private String hitmapJson;
}
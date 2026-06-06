package com.example.runsense.domain.dto.running_activity.sub;

import com.example.runsense.domain.entity.FootSensorData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "발 센서 데이터 조회 응답 DTO")
public class FootSensorDataResponseDto {

    @Schema(description = "센서 데이터 ID", example = "1")
    private Long sensorDataId;

    @Schema(description = "발 방향", example = "Left")
    private String footSide;

    @Schema(description = "회내유형", example = "과내선")
    private String pronationType;

    @Schema(description = "뒤꿈치 착지율", example = "99.0")
    private Double heelStrikeRate;

    @Schema(description = "히트맵 데이터", example = "{}")
    private String hitmapJson;

    public static FootSensorDataResponseDto from(FootSensorData footSensorData) {
        return FootSensorDataResponseDto.builder()
                .sensorDataId(footSensorData.getSensorDataId())
                .footSide(footSensorData.getFootSide())
                .pronationType(footSensorData.getPronationType())
                .heelStrikeRate(footSensorData.getHeelStrikeRate())
                .hitmapJson(footSensorData.getHitmapJson())
                .build();
    }
}
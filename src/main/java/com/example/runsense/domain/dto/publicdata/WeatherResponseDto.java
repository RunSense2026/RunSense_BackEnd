package com.example.runsense.domain.dto.publicdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "날씨 조회 응답 DTO")
public class WeatherResponseDto {

    @Schema(description = "기온(°C)", example = "23.5")
    private Double temperature;

    @Schema(description = "습도(%)", example = "45.0")
    private Double humidity;

    @Schema(description = "풍속(m/s)", example = "2.1")
    private Double windSpeed;

    @Schema(description = "강수형태 코드 (0:없음, 1/5:비, 2/6:비/눈, 3/7:눈)", example = "0")
    private String precipitationType;

    @Schema(description = "하늘상태 코드 (1:맑음, 3:구름많음, 4:흐림, 조회 실패 시 null)", example = "1")
    private String skyCondition;
}

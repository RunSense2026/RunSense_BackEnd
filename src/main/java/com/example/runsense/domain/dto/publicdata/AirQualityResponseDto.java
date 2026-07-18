package com.example.runsense.domain.dto.publicdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "대기질 조회 응답 DTO")
public class AirQualityResponseDto {

    @Schema(description = "측정소명 (좌표 기준 최근접 측정소)", example = "강남구")
    private String stationName;

    @Schema(description = "측정시각", example = "2026-07-19 04:00")
    private String dataTime;

    @Schema(description = "미세먼지 PM10 농도(㎍/㎥)", example = "32.0")
    private Double pm10Value;

    @Schema(description = "초미세먼지 PM2.5 농도(㎍/㎥)", example = "18.0")
    private Double pm25Value;

    @Schema(description = "통합대기환경지수", example = "55.0")
    private Double khaiValue;

    @Schema(description = "PM10 등급 (1:좋음, 2:보통, 3:나쁨, 4:매우나쁨)", example = "2")
    private String pm10Grade;

    @Schema(description = "PM2.5 등급 (1:좋음, 2:보통, 3:나쁨, 4:매우나쁨)", example = "2")
    private String pm25Grade;

    @Schema(description = "통합대기환경 등급 (1:좋음, 2:보통, 3:나쁨, 4:매우나쁨)", example = "2")
    private String khaiGrade;
}

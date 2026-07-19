package com.example.runsense.domain.dto.publicdata.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 에어코리아 실시간 측정정보(getMsrstnAcctoRltmMesureDnsty) 응답의 item 하나. */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirQualityItemDto {

    @JsonProperty("dataTime")
    private String dataTime;

    @JsonProperty("pm10Value")
    private String pm10Value;

    @JsonProperty("pm25Value")
    private String pm25Value;

    @JsonProperty("khaiValue")
    private String khaiValue;

    @JsonProperty("khaiGrade")
    private String khaiGrade;

    @JsonProperty("pm10Grade")
    private String pm10Grade;

    @JsonProperty("pm25Grade")
    private String pm25Grade;
}

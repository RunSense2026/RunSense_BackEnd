package com.example.runsense.domain.dto.publicdata.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 기상청 초단기실황/초단기예보 응답의 item 하나. */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KmaWeatherItemDto {

    @JsonProperty("category")
    private String category;

    /** 초단기실황(getUltraSrtNcst) 관측값 */
    @JsonProperty("obsrValue")
    private String obsrValue;

    /** 초단기예보(getUltraSrtFcst) 예보값 */
    @JsonProperty("fcstValue")
    private String fcstValue;

    /** 초단기예보 예보시각 (HHmm) */
    @JsonProperty("fcstTime")
    private String fcstTime;
}

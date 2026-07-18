package com.example.runsense.domain.dto.publicdata.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 에어코리아 근접측정소(getNearbyMsrstnList) 응답의 item 하나. */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NearbyStationItemDto {

    @JsonProperty("stationName")
    private String stationName;

    @JsonProperty("addr")
    private String addr;

    /** 입력 좌표로부터의 거리(km) */
    @JsonProperty("tm")
    private String tm;
}

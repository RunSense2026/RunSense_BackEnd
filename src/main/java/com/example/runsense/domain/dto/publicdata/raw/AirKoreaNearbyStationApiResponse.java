package com.example.runsense.domain.dto.publicdata.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** 에어코리아 근접측정소 API 원본 응답 구조 (response.body.items). */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirKoreaNearbyStationApiResponse {

    @JsonProperty("response")
    private Response response;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("body")
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @JsonProperty("items")
        private List<NearbyStationItemDto> items;
    }
}

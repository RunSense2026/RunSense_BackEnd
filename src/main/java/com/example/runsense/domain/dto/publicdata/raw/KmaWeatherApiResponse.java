package com.example.runsense.domain.dto.publicdata.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** 기상청 초단기실황/초단기예보 API 원본 응답 구조 (response.body.items.item). */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KmaWeatherApiResponse {

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
        private Items items;
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        @JsonProperty("item")
        private List<KmaWeatherItemDto> item;
    }
}

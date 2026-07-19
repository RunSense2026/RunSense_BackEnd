package com.example.runsense.domain.dto.publicdata.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** OpenStreetMap Nominatim 역지오코딩(reverse) API 원본 응답 구조. */
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimReverseGeocodeResponse {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("address")
    private Address address;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        @JsonProperty("city_district")
        private String cityDistrict;

        @JsonProperty("borough")
        private String borough;

        @JsonProperty("county")
        private String county;

        @JsonProperty("city")
        private String city;

        @JsonProperty("town")
        private String town;

        @JsonProperty("province")
        private String province;

        @JsonProperty("state")
        private String state;

        @JsonProperty("region")
        private String region;
    }
}

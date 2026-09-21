package com.example.runsense.domain.dto.course.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 전국도시공원정보표준데이터의 공원 1건. 원본 값은 숫자도 모두 문자열로 들어온다.
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkRecordDto {

    // 주의: 관리번호는 고유하지 않다 (지자체 통합 전·후 중복 제공 등).
    @JsonProperty("관리번호")
    private String managementNo;

    @JsonProperty("공원명")
    private String name;

    @JsonProperty("공원구분")
    private String type;

    @JsonProperty("소재지도로명주소")
    private String roadAddress;

    @JsonProperty("소재지지번주소")
    private String lotAddress;

    @JsonProperty("위도")
    private String latitude;

    @JsonProperty("경도")
    private String longitude;

    @JsonProperty("공원면적")
    private String area;

    @JsonProperty("공원보유시설(운동시설)")
    private String sportsFacilities;

    @JsonProperty("공원보유시설(유희시설)")
    private String playFacilities;

    @JsonProperty("공원보유시설(편익시설)")
    private String convenienceFacilities;

    @JsonProperty("공원보유시설(교양시설)")
    private String cultureFacilities;

    @JsonProperty("공원보유시설(기타시설)")
    private String etcFacilities;

    @JsonProperty("데이터기준일자")
    private String baseDate;
}

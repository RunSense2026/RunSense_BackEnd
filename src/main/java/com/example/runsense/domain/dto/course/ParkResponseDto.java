package com.example.runsense.domain.dto.course;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "추천 공원 DTO")
public class ParkResponseDto {

    @Schema(description = "추천 순위 (1부터)", example = "1")
    private int rank;

    @Schema(description = "공원명", example = "향동근린2")
    private String name;

    @Schema(description = "공원구분", example = "근린공원")
    private String type;

    @Schema(description = "소재지 주소 (도로명 우선, 없으면 지번)", example = "경기도 고양시 덕양구 향동로 200")
    private String address;

    @Schema(description = "위도", example = "37.6069348")
    private double latitude;

    @Schema(description = "경도", example = "126.8933163")
    private double longitude;

    @Schema(description = "공원면적(㎡), 정보 없으면 null", example = "11935.3")
    private Double area;

    @Schema(description = "현재 위치로부터 직선거리(km)", example = "0.42")
    private double distanceKm;

    @ArraySchema(arraySchema = @Schema(description = "표준 시설 태그 (시설 원문에서 추출, 없으면 빈 배열)"),
            schema = @Schema(example = "화장실", allowableValues = {"산책로", "운동기구", "구기장", "화장실", "음수대", "주차장", "휴식공간", "CCTV"}))
    private List<String> tags;

    @Schema(description = "보유시설 원문")
    private ParkFacilityDto facilities;
}

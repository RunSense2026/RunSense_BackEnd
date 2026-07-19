package com.example.runsense.domain.dto.publicdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "위치 역지오코딩 응답 DTO")
public class LocationResponseDto {

    @Schema(description = "화면 표시용 지역명", example = "서울 · 강남구")
    private String name;

    @Schema(description = "자치구/시군구명 (대기질 측정소명 폴백용)", example = "강남구")
    private String gu;
}

package com.example.runsense.domain.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "주변 공원 추천 응답 DTO")
public class ParkListResponseDto {

    @Schema(description = "가까운 순으로 정렬된 추천 공원")
    private List<ParkResponseDto> parks;
}

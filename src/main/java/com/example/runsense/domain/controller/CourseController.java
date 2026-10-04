package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.course.ParkListResponseDto;
import com.example.runsense.domain.service.CourseService;
import com.example.runsense.global.exception.ApiErrorAnnotation;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/course")
@Tag(name = "course", description = "러닝 코스 추천 엔드포인트")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "주변 공원 추천",
            description = "전국도시공원정보표준데이터 기준으로 현재 위치에서 직선거리가 가까운 공원을 순위대로 반환합니다. "
                    + "기본값은 면적 10,000㎡ 이상 공원 3곳이며, 묘지공원은 제외됩니다.")
    @GetMapping("/nearby")
    @ApiErrorAnnotation({ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<ParkListResponseDto> getNearbyParks(
            @Parameter(description = "위도", example = "37.6045") @RequestParam double lat,
            @Parameter(description = "경도", example = "126.8970") @RequestParam double lon,
            @Parameter(description = "추천 개수 (1~10)", example = "3") @RequestParam(defaultValue = "3") int limit,
            @Parameter(description = "최소 공원면적(㎡). 0이면 면적 필터 없음 (면적 정보 없는 공원도 포함)", example = "10000")
            @RequestParam(defaultValue = "10000") double minArea,
            @Parameter(description = "필수 시설 태그 — 모두 갖춘 공원만 반환 (시설 정보가 없는 공원이 많아 걸수록 후보가 크게 줄어듦). 미지정(null)이면 시설 필터 없음",
                    array = @ArraySchema(schema = @Schema(type = "string", allowableValues = {
                            "산책로", "운동기구", "구기장", "화장실", "음수대", "주차장", "휴식공간", "CCTV"})))
            @RequestParam(required = false) List<String> tags
    ) {
        return ApiResponse.success(courseService.getNearbyParks(lat, lon, limit, minArea, tags));
    }
}

package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.publicdata.AirQualityResponseDto;
import com.example.runsense.domain.dto.publicdata.LocationResponseDto;
import com.example.runsense.domain.dto.publicdata.WeatherResponseDto;
import com.example.runsense.domain.service.PublicDataService;
import com.example.runsense.global.exception.ApiErrorAnnotation;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@Tag(name = "public-data", description = "공공데이터(날씨/대기질/위치) 엔드포인트")
public class PublicDataController {

    private final PublicDataService publicDataService;

    public PublicDataController(PublicDataService publicDataService) {
        this.publicDataService = publicDataService;
    }

    @Operation(summary = "날씨 조회", description = "위경도 기준으로 기상청 초단기실황/초단기예보 데이터를 조회합니다.")
    @GetMapping("/weather")
    @ApiErrorAnnotation({ErrorCode.PUBLIC_API_ERROR})
    public ApiResponse<WeatherResponseDto> getWeather(
            @Parameter(description = "위도", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도", example = "126.9780") @RequestParam double lon
    ) {
        return ApiResponse.success(publicDataService.getWeather(lat, lon));
    }

    @Operation(summary = "대기질 조회", description = "위경도 기준으로 최근접 측정소의 에어코리아 실시간 측정정보를 조회합니다.")
    @GetMapping("/air-quality")
    @ApiErrorAnnotation({ErrorCode.PUBLIC_API_ERROR})
    public ApiResponse<AirQualityResponseDto> getAirQuality(
            @Parameter(description = "위도", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도", example = "126.9780") @RequestParam double lon
    ) {
        return ApiResponse.success(publicDataService.getAirQuality(lat, lon));
    }

    @Operation(summary = "위치 역지오코딩", description = "위경도 기준으로 화면 표시용 지역명을 조회합니다 (OpenStreetMap Nominatim).")
    @GetMapping("/location")
    @ApiErrorAnnotation({ErrorCode.PUBLIC_API_ERROR})
    public ApiResponse<LocationResponseDto> getLocation(
            @Parameter(description = "위도", example = "37.5665") @RequestParam double lat,
            @Parameter(description = "경도", example = "126.9780") @RequestParam double lon
    ) {
        return ApiResponse.success(publicDataService.getLocation(lat, lon));
    }
}

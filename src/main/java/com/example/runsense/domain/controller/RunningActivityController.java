package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityDetailResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.service.RunningActivityService;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/run")
public class RunningActivityController {

    private final RunningActivityService runningActivityService;

    public RunningActivityController(RunningActivityService runningActivityService) {
        this.runningActivityService = runningActivityService;
    }

    @Operation(summary = "러닝 기록 생성", description = "러닝 기록, 구간 기록, 발 센서 데이터, AI 코칭 정보를 함께 저장합니다")
    @PostMapping("/save")
    public ApiResponse<RunningActivityResponseDto> create(@Valid @RequestBody RunningActivityCreateRequestDto requestDto, @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(runningActivityService.create(requestDto, userId));
    }

    @Operation(summary = "러닝 기록 조회", description = "러닝 기록, 구간 기록, 발 센서 데이터, AI 코칭 정보를 함께 조회합니다.")
    @PostMapping("/read/{activityId}")
    public ApiResponse<RunningActivityDetailResponseDto> read(@PathVariable Long activityId, @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(runningActivityService.read(activityId, userId));
    }
}
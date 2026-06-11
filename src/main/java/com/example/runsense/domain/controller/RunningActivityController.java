package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityDetailResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityListResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.service.RunningActivityService;
import com.example.runsense.global.exception.ApiErrorAnnotation;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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
    @PostMapping
    @ApiErrorAnnotation({ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<RunningActivityResponseDto> create(@Valid @RequestBody RunningActivityCreateRequestDto requestDto, @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(runningActivityService.create(requestDto, userId));
    }

    @Operation(summary = "러닝 기록 조회", description = "러닝 기록, 구간 기록, 발 센서 데이터, AI 코칭 정보를 함께 조회합니다.")
    @GetMapping("/{activityId}")
    @ApiErrorAnnotation({ErrorCode.RUNNING_ACTIVITY_NOT_FOUND})
    public ApiResponse<RunningActivityDetailResponseDto> read(@PathVariable Long activityId, @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(runningActivityService.read(activityId, userId));
    }

    @Operation(summary = "모든 기간 러닝 기록 리스트 조회", description = "모든 기간의 러닝 기록을 조회합니다.")
    @GetMapping
    @ApiErrorAnnotation({ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<RunningActivityListResponseDto> readAll(@AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(runningActivityService.readAll(userId));
    }

    @Operation(summary = "특정 기간 내 러닝 기록 리스트 조회", description = "시작 날짜와 종료 날짜 사이의 러닝 기록을 조회합니다.")
    @GetMapping("/date")
    @ApiErrorAnnotation({ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<RunningActivityListResponseDto> readDate(@AuthenticationPrincipal(expression = "userId") Long userId, @Valid @ParameterObject @ModelAttribute RunningActivityRequestDto requestDto) {

        return ApiResponse.success(runningActivityService.readDate(userId, requestDto));
    }
}
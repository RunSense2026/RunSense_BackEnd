package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.service.RunningActivityService;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/running-activities")
public class RunningActivityController {

    private final RunningActivityService runningActivityService;

    public RunningActivityController(RunningActivityService runningActivityService) {
        this.runningActivityService = runningActivityService;
    }

    @Operation(summary = "러닝 기록 생성", description = "러닝 기록, 구간 기록, 발 센서 데이터, AI 코칭 정보를 함께 저장합니다")
    @PostMapping
    public ApiResponse<RunningActivityResponseDto> create(@Valid @RequestBody RunningActivityCreateRequestDto requestDto) {
        return ApiResponse.success(runningActivityService.create(requestDto));
    }
}
package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.dto.running_activity.detail.RunningActivityDetailResponseDto;
import com.example.runsense.domain.service.RunningActivityService;
import com.example.runsense.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/running-activities")
public class RunningActivityController {

	private final RunningActivityService runningActivityService;

	public RunningActivityController(RunningActivityService runningActivityService) {
		this.runningActivityService = runningActivityService;
	}

	@PostMapping
	public ApiResponse<RunningActivityResponseDto> create(@Valid @RequestBody RunningActivityCreateRequestDto requestDto) {
		return ApiResponse.success(runningActivityService.create(requestDto));
	}

	@GetMapping("/{activityId}")
	public ApiResponse<RunningActivityDetailResponseDto> read(@PathVariable Long activityId) {
		return ApiResponse.success(runningActivityService.read(activityId));
	}
}
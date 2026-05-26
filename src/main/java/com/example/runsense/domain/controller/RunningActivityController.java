package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityListResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.dto.running_activity.detail.RunningActivityDetailResponseDto;
import com.example.runsense.domain.service.RunningActivityService;
import com.example.runsense.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.runsense.global.security.UserPrincipal;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;

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

	@GetMapping("")
	public ApiResponse<RunningActivityListResponseDto> getList(@PageableDefault(size = 10, sort = "activityDate", direction = Sort.Direction.DESC) Pageable pageable) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!(principal instanceof UserPrincipal)) {
			throw new CustomException(ErrorCode.ACCESS_DENIED);
		}
		Long userId = ((UserPrincipal) principal).getUserId();

		return ApiResponse.success(runningActivityService.listByUser(userId, pageable));
	}

	@GetMapping("/{activityId}")
	public ApiResponse<RunningActivityDetailResponseDto> read(@PathVariable Long activityId) {
		return ApiResponse.success(runningActivityService.read(activityId));
	}
}
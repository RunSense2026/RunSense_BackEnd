package com.example.runsense.domain.dto.running_activity;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RunningActivityListResponseDto {
    private double totalDistance;    // 거리의 총합
    private double averageFormScore; // 폼 점수의 평균
    private int totalCount;          // 러닝 횟수

    private List<RunningActivityResponseDto> activities;
}
package com.example.runsense.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRunningStatisticsDto { // 통계 담는 Dto
    private Double totalDistance;
    private Long totalCount;
}
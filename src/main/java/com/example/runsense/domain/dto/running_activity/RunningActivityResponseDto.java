package com.example.runsense.domain.dto.running_activity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RunningActivityResponseDto {

    private Long activityId;
    private Long userId;
    private Double distance;
    private String time;
    private String pace;
    private LocalDateTime activityDate;
    private Short formScore;
    private int lapSplitCount;
    private int footSensorDataCount;
    private int aiCoachingCount;
}
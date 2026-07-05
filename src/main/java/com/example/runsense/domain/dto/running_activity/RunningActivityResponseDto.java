package com.example.runsense.domain.dto.running_activity;

import com.example.runsense.domain.dto.running_activity.sub.SummaryResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RunningActivityResponseDto {

    private Long activityId;
    private Double distance;
    private String time;
    private String pace;
    private LocalDateTime activityDate;
    private Short formScore;
    private SummaryResponseDto aiCoachingSummary; // 생성 시점에만 채워짐. 리포트가 저장되지 않으므로 조회 응답에서는 항상 null
}
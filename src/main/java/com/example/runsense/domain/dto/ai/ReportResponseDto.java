package com.example.runsense.domain.dto.ai;

import com.example.runsense.domain.dto.ai.sub.ReportItemDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "AI 요약 리포트 생성 응답 DTO")
public class ReportResponseDto {

    @Schema(description = "요약 제목", example = "오늘의 분석")
    @JsonProperty("summary_title")
    private String summaryTitle;

    @Schema(description = "전체 코칭 개수", example = "4")
    @JsonProperty("total_coaching_count")
    private Integer totalCoachingCount;

    @Schema(description = "요약 항목 목록")
    private List<ReportItemDto> items;
}

package com.example.runsense.domain.dto.running_activity.sub;

import com.example.runsense.domain.dto.ai.ReportResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// ReportResponseDto(FastAPI 응답, snake_case)를 우리 API의 camelCase 규격으로 변환한 응답 DTO
@Getter
@Builder
@Schema(description = "AI 코칭 요약 리포트 응답 DTO")
public class SummaryResponseDto {

    @Schema(description = "요약 제목", example = "오늘의 분석")
    private String summaryTitle;

    @Schema(description = "전체 코칭 개수", example = "4")
    private Integer totalCoachingCount;

    @Schema(description = "요약 항목 목록")
    private List<SummaryItemDto> items;

    public static SummaryResponseDto from(ReportResponseDto reportResponseDto) {
        List<SummaryItemDto> items = reportResponseDto.getItems().stream()
                .map(SummaryItemDto::from)
                .toList();

        return SummaryResponseDto.builder()
                .summaryTitle(reportResponseDto.getSummaryTitle())
                .totalCoachingCount(reportResponseDto.getTotalCoachingCount())
                .items(items)
                .build();
    }
}

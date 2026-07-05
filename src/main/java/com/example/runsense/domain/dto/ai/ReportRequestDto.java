package com.example.runsense.domain.dto.ai;

import com.example.runsense.domain.dto.ai.sub.CoachingLogDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

// FastAPI로 보내는 요청 바디. 컨트롤러에는 노출되지 않고 RunningActivityService에서만 생성됨.
@Getter
@Builder
@Schema(description = "AI 요약 리포트 생성 요청 DTO")
public class ReportRequestDto {

    @Schema(description = "요약할 코칭 로그 목록")
    @JsonProperty("coaching_logs")
    private List<CoachingLogDto> coachingLogs;
}

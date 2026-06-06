package com.example.runsense.domain.dto.running_activity.sub;

import com.example.runsense.domain.entity.LapSplit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "구간 기록 조회 응답 DTO")
public class LapSplitResponseDto {

    @Schema(description = "구간 ID", example = "1")
    private Long splitId;

    @Schema(description = "구간 순서", example = "1")
    private Long lapNumber;

    @Schema(description = "구간 페이스", example = "5'00")
    private String lapPace;

    @Schema(description = "평균 심박수", example = "170")
    private Integer avgHeartRate;

    @Schema(description = "페이스 상태", example = "Green")
    private String paceStatue;

    public static LapSplitResponseDto from(LapSplit lapSplit) {
        return LapSplitResponseDto.builder()
                .splitId(lapSplit.getSplitId())
                .lapNumber(lapSplit.getLapNumber())
                .lapPace(lapSplit.getLapPace())
                .avgHeartRate(lapSplit.getAvgHeartRate())
                .paceStatue(lapSplit.getPaceStatue())
                .build();
    }
}
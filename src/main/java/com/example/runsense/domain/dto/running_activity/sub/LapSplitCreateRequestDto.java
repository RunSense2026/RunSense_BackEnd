package com.example.runsense.domain.dto.running_activity.sub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "구간별 기록 요청 DTO")
public class LapSplitCreateRequestDto {

    @Schema(description = "구간 순서. null이면 DB에 NULL로 저장", example = "1")
    private Long lapNumber;

    @Schema(description = "구간 페이스. null이면 DB에 NULL로 저장", example = "5'00")
    private String lapPace;

    @Schema(description = "평균 심박수. null이면 DB에 NULL로 저장", example = "170")
    private Integer avgHeartRate;

    @Schema(description = "페이스 상태. null이면 DB에 NULL로 저장", example = "Green")
    private String paceStatue;
}
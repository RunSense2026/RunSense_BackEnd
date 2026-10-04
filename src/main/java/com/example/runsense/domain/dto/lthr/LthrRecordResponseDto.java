package com.example.runsense.domain.dto.lthr;

import com.example.runsense.domain.entity.LthrRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
@Schema(description = "LTHR 테스트 기록 응답 DTO")
public class LthrRecordResponseDto {

    // 마지막 측정 후 이 일수(6주)가 지나면 재측정을 권장한다.
    public static final int RETEST_INTERVAL_DAYS = 42;

    @Schema(description = "기록 ID", example = "1")
    private Long recordId;

    @Schema(description = "측정된 LTHR(bpm)", example = "162")
    private Integer lthr;

    @Schema(description = "직전 테스트의 LTHR(bpm). 첫 측정이면 null", example = "158")
    private Integer previousLthr;

    @Schema(description = "직전 테스트 대비 증감(bpm). 첫 측정이면 null", example = "4")
    private Integer lthrDifference;

    @Schema(description = "테스트 전 안정 측정 단계의 평균 심박수(bpm)", example = "58.0")
    private Double restingHeartRate;

    @Schema(description = "테스트 중 최고 심박수(bpm)", example = "174")
    private Integer maxHeartRate;

    @Schema(description = "테스트 중 평균 심박수(bpm)", example = "158.4")
    private Double averageHeartRate;

    @Schema(description = "측정 일시")
    private LocalDateTime measuredAt;

    @Schema(description = "측정일로부터 지난 일수", example = "70")
    private Long daysSinceMeasured;

    @Schema(description = "재측정 권장 여부 (측정 후 6주 이상 경과)", example = "true")
    private Boolean recommendRetest;

    public static LthrRecordResponseDto from(LthrRecord record, LocalDate today) {
        long days = ChronoUnit.DAYS.between(record.getMeasuredAt().toLocalDate(), today);
        Integer difference = record.getPreviousLthr() == null
                ? null
                : record.getLthr() - record.getPreviousLthr();

        return LthrRecordResponseDto.builder()
                .recordId(record.getRecordId())
                .lthr(record.getLthr())
                .previousLthr(record.getPreviousLthr())
                .lthrDifference(difference)
                .restingHeartRate(record.getRestingHeartRate())
                .maxHeartRate(record.getMaxHeartRate())
                .averageHeartRate(record.getAverageHeartRate())
                .measuredAt(record.getMeasuredAt())
                .daysSinceMeasured(days)
                .recommendRetest(days >= RETEST_INTERVAL_DAYS)
                .build();
    }
}

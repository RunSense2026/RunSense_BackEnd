package com.example.runsense.domain.dto.lthr;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "LTHR 테스트 결과 저장 요청 DTO")
public class LthrRecordCreateRequestDto {

    @Schema(description = "측정된 LTHR(bpm)", example = "162")
    @NotNull(message = "lthr는 필수 입력 값입니다.")
    @Positive(message = "lthr는 0보다 커야 합니다.")
    private Integer lthr;

    @Schema(description = "테스트 전 안정 측정 단계의 평균 심박수(bpm)", example = "58.0")
    @NotNull(message = "restingHeartRate는 필수 입력 값입니다.")
    @Positive(message = "restingHeartRate는 0보다 커야 합니다.")
    private Double restingHeartRate;

    @Schema(description = "테스트 중 최고 심박수(bpm). null이면 저장하지 않음", example = "174")
    @Positive(message = "maxHeartRate는 0보다 커야 합니다.")
    private Integer maxHeartRate;

    @Schema(description = "테스트 중 평균 심박수(bpm). null이면 저장하지 않음", example = "158.4")
    @Positive(message = "averageHeartRate는 0보다 커야 합니다.")
    private Double averageHeartRate;
}

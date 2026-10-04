package com.example.runsense.domain.dto.lthr;

import com.example.runsense.domain.entity.LthrRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LthrRecordResponseDtoTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 4);

    private static LthrRecord record(Integer lthr, Integer previousLthr, LocalDateTime measuredAt) {
        return LthrRecord.builder()
                .recordId(1L)
                .lthr(lthr)
                .previousLthr(previousLthr)
                .measuredAt(measuredAt)
                .build();
    }

    @Test
    void 측정_후_6주_미만이면_재측정을_권장하지_않는다() {
        LthrRecordResponseDto dto = LthrRecordResponseDto.from(
                record(162, null, TODAY.minusDays(41).atTime(23, 59)), TODAY);

        assertThat(dto.getDaysSinceMeasured()).isEqualTo(41);
        assertThat(dto.getRecommendRetest()).isFalse();
    }

    @Test
    void 측정_후_6주가_지나면_재측정을_권장한다() {
        LthrRecordResponseDto dto = LthrRecordResponseDto.from(
                record(162, null, TODAY.minusDays(42).atTime(0, 1)), TODAY);

        assertThat(dto.getDaysSinceMeasured()).isEqualTo(42);
        assertThat(dto.getRecommendRetest()).isTrue();
    }

    @Test
    void 직전_기록이_있으면_증감을_계산하고_없으면_null이다() {
        LocalDateTime measuredAt = TODAY.atStartOfDay();

        assertThat(LthrRecordResponseDto.from(record(162, 158, measuredAt), TODAY).getLthrDifference()).isEqualTo(4);
        assertThat(LthrRecordResponseDto.from(record(155, 158, measuredAt), TODAY).getLthrDifference()).isEqualTo(-3);
        assertThat(LthrRecordResponseDto.from(record(162, null, measuredAt), TODAY).getLthrDifference()).isNull();
    }
}

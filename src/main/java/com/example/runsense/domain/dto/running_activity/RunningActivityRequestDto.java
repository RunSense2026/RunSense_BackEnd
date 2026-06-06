package com.example.runsense.domain.dto.running_activity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "러닝 액티비티 리스트 기간 조회 요청 DTO")
public class RunningActivityRequestDto {
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "시작 날짜", example = "2026-06-01")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "종료 날짜", example = "2026-06-07")
    private LocalDate endDate;
}

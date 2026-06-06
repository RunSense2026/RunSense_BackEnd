package com.example.runsense.domain.dto.running_activity;

import com.example.runsense.domain.dto.running_activity.sub.AiCoachingCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.sub.FootSensorDataCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.sub.LapSplitCreateRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "러닝 액티비티 저장 요청 DTO")
public class RunningActivityCreateRequestDto {

    @Schema(description = "총 거리", example = "5.00")
    @NotNull(message = "distance는 필수 입력 값입니다.")
    private Double distance;

    @Schema(description = "총 시간", example = "00:30:00")
    @NotNull(message = "time은 필수 입력 값입니다.")
    private String time;

    @Schema(description = "평균 페이스", example = "5'00")
    @NotNull(message = "pace는 필수 입력 값입니다.")
    private String pace;

    @Schema(description = "폼 점수", example = "100")
    @NotNull(message = "formScore은 필수 입력 값입니다.")
    private Short formScore;

    @Schema(description = "러닝 액티비티에 포함된 lapSplits 리스트", example = "[{\"lapNumber\":1,\"lapPace\":\"5'00\",\"avgHeartRate\":170,\"paceStatue\":\"Green\"},{\"lapNumber\":2,\"lapPace\":\"5'10\",\"avgHeartRate\":168,\"paceStatue\":\"Yellow\"}]")
    @Valid
    @NotNull(message = "lapSplits는 필수 입력 값입니다.")
    @Size(max = 5, message = "lapSplit은 최대 5개까지 저장할 수 있습니다.")
    private List<LapSplitCreateRequestDto> lapSplits;

    @Schema(description = "러닝 액티비티에 포함된 footSensorData 리스트", example = "[{\"footSide\":\"Left\",\"pronationType\":\"과내선\",\"heelStrikeRate\":99.0,\"hitmapJson\":\"{\\\"heel\\\":40,\\\"midfoot\\\":35,\\\"forefoot\\\":25}\"},{\"footSide\":\"Right\",\"pronationType\":\"정상\",\"heelStrikeRate\":97.5,\"hitmapJson\":\"{\\\"heel\\\":38,\\\"midfoot\\\":36,\\\"forefoot\\\":26}\"}]")
    @Valid
    @NotNull(message = "footSensorData는 필수 입력 값입니다.")
    @Size(min = 2, max = 2, message = "footSensorData는 정확히 2개여야 합니다.")
    private List<FootSensorDataCreateRequestDto> footSensorData;

    @Schema(description = "러닝 액티비티에 포함된 aiCoachings 리스트", example = "[{\"feedbackText\":\"호흡을 가다듬고 페이스를 조금 낮춰보세요.\",\"focusPoint\":\"호흡 안정과 페이스 조절\",\"recExercise\":\"가벼운 조깅과 호흡 조절 훈련\"},{\"feedbackText\":\"마지막 구간에서 보폭이 줄었습니다.\",\"focusPoint\":\"후반 보폭 유지\",\"recExercise\":\"스트라이드 드릴과 하체 근지구력 훈련\"}]")
    @Valid
    @NotNull(message = "aiCoachings는 필수 입력 값입니다.")
    @Size(max = 3, message = "aiCoaching은 최대 3개까지 저장할 수 있습니다.")
    private List<AiCoachingCreateRequestDto> aiCoachings;
}
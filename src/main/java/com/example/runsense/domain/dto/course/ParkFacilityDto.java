package com.example.runsense.domain.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "공원 보유시설 원문 (공공데이터 값 그대로, 없으면 null)")
public class ParkFacilityDto {

    @Schema(description = "운동시설", example = "체력단련시설 4개소, 배드민턴장")
    private String sports;

    @Schema(description = "유희시설", example = "조합놀이대+흔들놀이기구")
    private String play;

    @Schema(description = "편익시설", example = "화장실2, 주차장, 파고라")
    private String convenience;

    @Schema(description = "교양시설", example = "야외무대")
    private String culture;

    @Schema(description = "기타시설", example = "산책로, 광장")
    private String etc;
}

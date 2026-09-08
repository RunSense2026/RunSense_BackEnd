package com.example.runsense.domain.dto.ai;

import com.example.runsense.domain.dto.ai.sub.FootDataDto;
import com.example.runsense.domain.dto.ai.sub.ProfileDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

// AI 서버로 실제 전송되는 요청 바디. 컨트롤러에는 노출되지 않고 AiService에서만 생성됨.
// 클라이언트가 보낸 CoachingRequestDto에 서버가 조회한 유저 프로필을 더해 만든다.
@Getter
@Builder
public class AiCoachingRequestDto {

    private Double pace;

    @JsonProperty("heart_rate")
    private Integer heartRate;

    private Integer cadence;

    @JsonProperty("left_foot")
    private FootDataDto leftFoot;

    @JsonProperty("right_foot")
    private FootDataDto rightFoot;

    private ProfileDto profile;
}

package com.example.runsense.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 유저 정보 수정 요청 시 사용자로부터 전달받는 데이터를 담는 DTO
 * 전달되지 않은(null) 필드는 수정하지 않는다.
 * 안정시 심박수·LTHR은 LTHR 테스트 결과(POST /api/lthr)로만 갱신되므로 여기서 받지 않는다.
 */
@Getter
@NoArgsConstructor
@Schema(description = "유저 정보 수정 요청 DTO")
public class UserUpdateRequestDto {
    @Schema(description = "사용자 이름. null이면 기존 값 유지", example = "홍길동")
    @Size(min = 2, max = 5, message = "이름은 2자 이상 5자 이하로 입력해주세요.")
    private String userName;

    @Schema(description = "생일. null이면 기존 값 유지", example = "2000-01-01")
    @Past(message = "생일은 과거 날짜여야 합니다.")
    private LocalDate birthDate;

    @Schema(description = "성별 (M, F, ETC 중 하나). null이면 기존 값 유지", example = "M")
    @Pattern(regexp = "^(M|F|ETC)$", message = "성별은 M, F, ETC 중 하나여야 합니다.")
    private String gender;

    @Schema(description = "신장. null이면 기존 값 유지", example = "170.0")
    @Positive(message = "신장은 0보다 커야 합니다.")
    private Double height;

    @Schema(description = "체중. null이면 기존 값 유지", example = "70.0")
    @Positive(message = "체중은 0보다 커야 합니다.")
    private Double weight;
}

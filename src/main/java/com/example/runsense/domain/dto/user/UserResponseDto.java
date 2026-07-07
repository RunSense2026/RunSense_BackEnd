package com.example.runsense.domain.dto.user;

import com.example.runsense.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "회원가입 성공 응답 DTO")
public class UserResponseDto {

	@Schema(description = "회원 ID", example = "1")
	private Long userId;
	@Schema(description = "사용자 이름", example = "홍길동")
	private String userName;
	@Schema(description = "이메일", example = "user@example.com")
	private String email;
	@Schema(description = "생년월일")
	private LocalDate birthDate;
	@Schema(description = "성별", example = "M")
	private String gender;
	@Schema(description = "키", example = "175.5")
	private Double height;
	@Schema(description = "체중", example = "70.0")
	private Double weight;
	@Schema(description = "이메일 인증 여부", example = "true")
	private Boolean isVerified;
	@Schema(description = "생성일")
	private LocalDate createdAt;

	// Entity를 DTO로 변환하는 정적 메서드
	public static UserResponseDto from(User user) {
		return UserResponseDto.builder()
				.userId(user.getUserId())
				.email(user.getEmail())
				.userName(user.getUserName())
				.birthDate(user.getBirthDate())
				.gender(user.getGender())
				.height(user.getHeight())
				.weight(user.getWeight())
				.isVerified(user.getIsVerified())
				.createdAt(user.getCreatedAt())
				.build();
	}
}
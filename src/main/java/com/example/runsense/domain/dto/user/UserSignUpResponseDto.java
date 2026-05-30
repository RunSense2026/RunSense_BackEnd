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
public class UserSignUpResponseDto {

	@Schema(description = "사용자 이름", example = "홍길동")
	private String userName;
	@Schema(description = "이메일", example = "user@example.com")
	private String email;
	@Schema(description = "이메일 인증 여부", example = "true")
	private Boolean isVerified;
	@Schema(description = "생성일")
	private LocalDate createdAt;

	public static UserSignUpResponseDto from(User user) {
		return UserSignUpResponseDto.builder()
				.userName(user.getUserName())
				.email(user.getEmail())
				.isVerified(user.getIsVerified())
				.createdAt(user.getCreatedAt())
				.build();
	}
}
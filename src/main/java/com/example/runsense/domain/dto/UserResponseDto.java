package com.example.runsense.domain.dto;

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

	private Long userId;
	private String userName;
	private String email;
	private LocalDate birthDate;
	private String gender;
	private Double height;
	private Boolean isVerified;
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
				.isVerified(user.getIsVerified())
				.createdAt(user.getCreatedAt())
				.build();
	}
}
package com.example.runsense.domain.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class UserLoginRequestDto {

	@Schema(description = "사용자 이메일", example = "user@example.com")
	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@Schema(description = "사용자 비밀번호", example = "password123")
	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	private String loginPw;
}
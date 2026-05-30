package com.example.runsense.domain.dto.user;

import com.example.runsense.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 회원가입 요청 시 사용자로부터 전달받는 데이터를 담는 DTO
 */
@Getter
@NoArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class UserSignUpRequestDto {

	@Schema(description = "로그인 비밀번호", example = "password123")
	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
	private String loginPw;

	@Schema(description = "사용자 이름", example = "홍길동")
	@NotBlank(message = "이름은 필수 입력 값입니다.")
	@Size(min = 2, max = 5, message = "이름은 2자 이상 5자 이하로 입력해주세요.")
	private String userName;

	@Schema(description = "이메일", example = "user@example.com")
	@NotBlank(message = "이메일은 필수 입력 값입니다.")
	@Email(message = "이메일 형식이 올바르지 않습니다.")
	private String email;

	@Schema(description = "이메일 인증 여부", example = "true")
	@AssertTrue(message = "이메일 인증이 필요합니다.")
	private Boolean isVerified;

	/**
	 * DTO 데이터로 엔티티 생성
	 * @param encodedPassword 암호화 된 비밀번호
	 * @return 가입 정보로 생성한 User 엔티티
	 */
	public User toEntity(String encodedPassword) {
		return User.builder()
				.loginPw(encodedPassword)
				.userName(this.userName)
				.email(this.email)
				.isVerified(this.isVerified)
				.createdAt(LocalDate.now())
				.build();
	}
}
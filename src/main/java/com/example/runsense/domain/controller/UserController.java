package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.user.*;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.runsense.domain.service.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(name="users", description = "users 엔드포인트")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * 회원가입 API
	 * @param requestDto 회원가입 정보가 들어있는 DTO
	 * @return 회원가입 성공시 유저 정보가 담긴 DTO 응답
	 */
	@Operation(summary = "유저 회원가입", description="유저가 요청한 정보로 회원가입을 진행합니다")
	@PostMapping("/signup")
	public ApiResponse<UserSignUpResponseDto> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
		UserSignUpResponseDto responseDto = userService.signUp(requestDto);
		return ApiResponse.success(responseDto);
	}

	/**
	 * 이메일 중복 확인 API
	 * @param email 확인할 이메일
	 * @return 중복 여부 응답
	 */
	@Operation(summary = "이메일 중복 체크", description="email이 존재하는지 조회하여 반환합니다")
	@GetMapping("/email-duplicate")
	public ApiResponse<EmailDuplicateCheckResponseDto> checkEmailDuplicate(@RequestParam String email) {
		boolean isDuplicated = userService.isEmailDuplicated(email);
		return ApiResponse.success(new EmailDuplicateCheckResponseDto(isDuplicated));
	}

	/**
	 * 로그인 API
	 * @param requestDto 이메일과 비밀번호가 들어있는 DTO
	 * @return 로그인 성공시 accessToken, refreshToken과 만료시간이 담긴 응답
	 */
	@Operation(summary = "유저 로그인", description = "이메일과 비밀번호로 로그인합니다")
	@PostMapping("/login")
	public ApiResponse<UserLoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto requestDto) {
		UserLoginResponseDto responseDto = userService.login(requestDto);
		return ApiResponse.success(responseDto);
	}

	/**
	 * 토큰 재발급 API
	 * @param requestDto refreshToken이 들어있는 DTO
	 * @return 새로운 accessToken, refreshToken과 만료시간이 담긴 응답
	 */
	@Operation(summary = "토큰 재발급", description = "refreshToken으로 accessToken과 refreshToken을 다시 발급합니다")
	@PostMapping("/reissue")
	public ApiResponse<UserTokenResponseDto> reissue(@Valid @RequestBody TokenReissueRequestDto requestDto) {
		UserTokenResponseDto responseDto = userService.reissue(requestDto.getRefreshToken());
		return ApiResponse.success(responseDto);
	}

	/**
	 * 로그아웃 API
	 * @param requestDto 로그아웃할 refreshToken이 들어있는 DTO
	 * @return 로그아웃 성공 응답
	 */
	@Operation(summary = "유저 로그아웃", description = "refreshToken을 무효화하고 로그아웃합니다")
	@PostMapping("/logout")
	public ApiResponse<Void> logout(@Valid @RequestBody TokenLogoutRequestDto requestDto) {
		userService.logout(requestDto.getRefreshToken());
		return ApiResponse.success(null);
	}
}

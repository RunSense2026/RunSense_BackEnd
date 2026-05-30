package com.example.runsense.domain.controller;

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

import com.example.runsense.domain.dto.EmailDuplicateCheckResponseDto;
import com.example.runsense.domain.dto.UserSignUpResponseDto;
import com.example.runsense.domain.dto.UserSignUpRequestDto;
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
}


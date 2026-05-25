package com.example.runsense.domain.controller;

import com.example.runsense.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.runsense.domain.dto.EmailDuplicateCheckResponseDto;
import com.example.runsense.domain.dto.UserResponseDto;
import com.example.runsense.domain.dto.UserSignUpRequestDto;
import com.example.runsense.domain.service.UserService;

@RestController
@RequestMapping("/api/users")
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
	@PostMapping("/signup")
	public ApiResponse<UserResponseDto> signUp(@Valid @RequestBody UserSignUpRequestDto requestDto) {
		UserResponseDto responseDto =  userService.signUp(requestDto);
		return ApiResponse.success(responseDto);
	}

	/**
	 * 이메일 중복 확인 API
	 * @param email 확인할 이메일
	 * @return 중복 여부 응답
	 */
	@GetMapping("/email-duplicate")
	public ApiResponse<EmailDuplicateCheckResponseDto> checkEmailDuplicate(@RequestParam String email) {
		boolean isDuplicated = userService.isEmailDuplicated(email);
		return ApiResponse.success(new EmailDuplicateCheckResponseDto(isDuplicated));
	}
}
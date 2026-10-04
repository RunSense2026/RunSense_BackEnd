package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.user.*;
import com.example.runsense.global.exception.ApiErrorAnnotation;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
	@ApiErrorAnnotation({ErrorCode.DUPLICATE_EMAIL, ErrorCode.INVALID_INPUT_VALUE})
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
	@ApiErrorAnnotation({ErrorCode.INVALID_LOGIN_INFO})
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
	@ApiErrorAnnotation({ErrorCode.REFRESH_TOKEN_NOT_FOUND})
	public ApiResponse<UserTokenResponseDto> reissue(@Valid @RequestBody TokenReissueRequestDto requestDto) {
		UserTokenResponseDto responseDto = userService.reissue(requestDto.getRefreshToken());
		return ApiResponse.success(responseDto);
	}

	/**
	 * 내 프로필 조회 API
	 * @param userId 인증된 유저의 ID
	 * @return 현재 유저 정보가 담긴 DTO 응답
	 */
	@Operation(summary = "내 프로필 조회", description = "인증된 유저 본인의 프로필 정보를 조회합니다")
	@GetMapping
	@ApiErrorAnnotation({ErrorCode.USER_NOT_FOUND})
	public ApiResponse<UserResponseDto> getMyProfile(@AuthenticationPrincipal(expression = "userId") Long userId) {
		UserResponseDto responseDto = userService.getMyProfile(userId);
		return ApiResponse.success(responseDto);
	}

	/**
	 * 유저 정보 업데이트 API
	 * @param userId 인증된 유저의 ID
	 * @param requestDto 수정할 유저 정보가 담긴 DTO (전달되지 않은 필드는 수정하지 않음)
	 * @return 수정된 유저 정보가 담긴 DTO 응답
	 */
	@Operation(summary = "유저 정보 업데이트", description = "수정할 유저 정보를 받아 해당 내용으로 업데이트 합니다. 값이 null인 필드는 수정하지 않습니다. 안정시 심박수·LTHR은 수정할 수 없으며 LTHR 테스트 결과(POST /api/lthr)로만 갱신됩니다.")
	@PatchMapping
	@ApiErrorAnnotation({ErrorCode.USER_NOT_FOUND, ErrorCode.INVALID_INPUT_VALUE})
	public ApiResponse<UserResponseDto> update(@AuthenticationPrincipal(expression = "userId") Long userId, @Valid @RequestBody UserUpdateRequestDto requestDto) {
		UserResponseDto responseDto = userService.update(userId, requestDto);
		return ApiResponse.success(responseDto);
	}

    /**
	 * 로그아웃 API
	 * @param requestDto 로그아웃할 refreshToken이 들어있는 DTO
	 * @return 로그아웃 성공 응답
	 */
	@Operation(summary = "유저 로그아웃", description = "refreshToken을 무효화하고 로그아웃합니다")
	@PostMapping("/logout")
	@ApiErrorAnnotation({ErrorCode.INVALID_LOGIN_INFO})
	public ApiResponse<Void> logout(@Valid @RequestBody TokenLogoutRequestDto requestDto) {
		userService.logout(requestDto.getRefreshToken());
		return ApiResponse.success(null);
	}
}

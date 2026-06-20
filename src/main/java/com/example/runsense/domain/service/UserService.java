package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.user.*;
import com.example.runsense.domain.entity.RefreshToken;
import com.example.runsense.domain.entity.RunningActivity;
import com.example.runsense.domain.repository.RefreshTokenRepository;
import com.example.runsense.domain.repository.RunningActivityRepository;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.runsense.domain.entity.User;
import com.example.runsense.domain.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RunningActivityRepository runningActivityRepository;

	public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RunningActivityRepository runningActivityRepository) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.runningActivityRepository = runningActivityRepository;
	}

	public boolean isEmailDuplicated(String email) {
		if (email == null || email.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
		}

		return userRepository.existsByEmail(email);
	}

	@Transactional
	public UserSignUpResponseDto signUp(UserSignUpRequestDto requestDto) {
		// 중복 이메일 여부 확인
		if (userRepository.existsByEmail(requestDto.getEmail())) { // 사용자가 입력한 이메일이 이미 저장되어 있다면
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(requestDto.getLoginPw());

		User user = requestDto.toEntity(encodedPassword); // Builder로 DTO -> 엔티티 변환
		User savedUser = userRepository.save(user); // DB에 저장

		return UserSignUpResponseDto.from(savedUser); // 엔티티 -> DTO로 재변환해서 리턴
	}

	@Transactional
	public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
		User user = userRepository.findByEmail(requestDto.getEmail())
				.orElseThrow(() -> new CustomException(ErrorCode.INVALID_LOGIN_INFO));

		if (!passwordEncoder.matches(requestDto.getLoginPw(), user.getLoginPw())) {
			throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
		}

		UserRunningStatisticsDto stats = runningActivityRepository.findStatisticsByUserId(user.getUserId());

		// DB 조회 결과가 null일 경우 대비
		int totalCount = stats.getTotalCount() != null ? stats.getTotalCount().intValue() : 0;
		double totalDistance = stats.getTotalDistance() != null ? stats.getTotalDistance() : 0.0;
		totalDistance = Math.round(totalDistance * 10) / 10.0;

		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = jwtTokenProvider.createRefreshToken(user);

		RefreshToken storedRefreshToken = refreshTokenRepository.findByUser(user)
				.orElseGet(() -> RefreshToken.create(user, null, null));
		storedRefreshToken.issue(refreshToken, jwtTokenProvider.getRefreshTokenExpiresAt());
		refreshTokenRepository.save(storedRefreshToken);

		return UserLoginResponseDto.of(user.getUserId(), user.getUserName(), user.getEmail(), user.getCreatedAt(), totalDistance, totalCount, accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiresInSeconds());
	}

	@Transactional
	public UserTokenResponseDto reissue(String refreshTokenValue) {
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
				.orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

		// Refresh Token의 만료 여부 확인 (DB에 저장된 expiresAt 기반)
		if (refreshToken.isExpired()) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		}

		// JWT 서명과 클레임 검증 (토큰 타입, 만료시간 등)
		jwtTokenProvider.validateRefreshToken(refreshTokenValue);

		User user = refreshToken.getUser();
		String newAccessToken = jwtTokenProvider.createAccessToken(user);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

		// 토큰 회전(rotation): 기존 토큰을 새 토큰으로 교체 (보안 강화)
		refreshToken.issue(newRefreshToken, jwtTokenProvider.getRefreshTokenExpiresAt());
		refreshTokenRepository.save(refreshToken);

		return UserTokenResponseDto.of(newAccessToken, newRefreshToken, jwtTokenProvider.getAccessTokenExpiresInSeconds());
	}

	@Transactional
	public void logout(String refreshTokenValue) {
		RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
				.orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
		refreshToken.clear();
		refreshTokenRepository.save(refreshToken);
	}
}
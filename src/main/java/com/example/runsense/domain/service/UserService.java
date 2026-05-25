package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.AuthTokenResponseDto;
import com.example.runsense.domain.dto.UserLoginRequestDto;
import com.example.runsense.domain.entity.RefreshToken;
import com.example.runsense.domain.repository.RefreshTokenRepository;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.runsense.domain.dto.UserSignUpRequestDto;
import com.example.runsense.domain.dto.UserSignUpResponseDto;
import com.example.runsense.domain.dto.UserResponseDto;
import com.example.runsense.domain.entity.User;
import com.example.runsense.domain.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
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
	public AuthTokenResponseDto login(UserLoginRequestDto requestDto) {
		User user = userRepository.findByEmail(requestDto.getEmail())
				.orElseThrow(() -> new CustomException(ErrorCode.INVALID_LOGIN_INFO));

		if (!passwordEncoder.matches(requestDto.getLoginPw(), user.getLoginPw())) {
			throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
		}

		String accessToken = jwtTokenProvider.createAccessToken(user);
		String refreshToken = jwtTokenProvider.createRefreshToken(user);

		// Refresh Token은 서버에서도 저장해야 함 (만료 확인, 토큰 회전 추적, 강제 로그아웃 등에 사용)
		refreshTokenRepository.save(RefreshToken.create(user, refreshToken, jwtTokenProvider.getRefreshTokenExpiresAt()));

		return AuthTokenResponseDto.of(accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiresInSeconds());
	}

	@Transactional
	public AuthTokenResponseDto reissue(String refreshTokenValue) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
				.orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

		// Refresh Token의 만료 여부 확인 (DB에 저장된 expiresAt 기반)
		if (refreshToken.isExpired()) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		}

		// Refresh Token의 폐기(revoke) 여부 확인 (강제 로그아웃 등으로 폐기된 경우)
		if (refreshToken.isRevoked()) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		// JWT 서명과 클레임 검증 (토큰 타입, 만료시간 등)
		jwtTokenProvider.validateRefreshToken(refreshTokenValue);

		User user = refreshToken.getUser();
		String newAccessToken = jwtTokenProvider.createAccessToken(user);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

		// 토큰 회전(rotation): 기존 토큰을 새 토큰으로 교체 (보안 강화)
		refreshToken.rotate(newRefreshToken, jwtTokenProvider.getRefreshTokenExpiresAt());
		refreshTokenRepository.save(refreshToken);

		return AuthTokenResponseDto.of(newAccessToken, newRefreshToken, jwtTokenProvider.getAccessTokenExpiresInSeconds());
	}
}
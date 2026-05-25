package com.example.runsense.global.security;

import com.example.runsense.domain.entity.User;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Component
public class JwtTokenProvider {
	private static final String TOKEN_TYPE = "type";
	private static final String TOKEN_TYPE_ACCESS = "ACCESS";
	private static final String TOKEN_TYPE_REFRESH = "REFRESH";
	private static final String USER_ID = "userId";

	private final SecretKey secretKey;
	private final long accessTokenExpirationMs;
	private final long refreshTokenExpirationMs;

	/**
	 * application.yaml에서 JWT 설정값을 주입받아 초기화
	 * 
	 * @param secret JWT 서명에 사용할 시크릿 키
	 * @param accessTokenExpirationMs Access Token 만료 시간 (기본값: 3600000ms = 1시간)
	 * @param refreshTokenExpirationMs Refresh Token 만료 시간 (기본값: 1209600000ms = 14일)
	 */
	public JwtTokenProvider(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.access-token-expiration-ms:3600000}") long accessTokenExpirationMs,
			@Value("${app.jwt.refresh-token-expiration-ms:1209600000}") long refreshTokenExpirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(resolveSecretBytes(secret));
		this.accessTokenExpirationMs = accessTokenExpirationMs;
		this.refreshTokenExpirationMs = refreshTokenExpirationMs;
	}

	/**
	 * API 요청 시 사용하는 Access Token 생성 (1시간)
	 * 
	 * @param user 토큰을 생성할 사용자
	 * @return JWT 형식의 Access Token 문자열
	 */
	public String createAccessToken(User user) {
		Date now = new Date();
		// 현재 시간 + accessTokenExpirationMs(기본 3600000ms = 1시간)로 만료시간 설정
		Date expiresAt = new Date(now.getTime() + accessTokenExpirationMs);

		return Jwts.builder()
				// subject: 사용자 이메일 (토큰 소유자)
				.subject(user.getEmail())
				// unique ID: 토큰 중복 방지
				.id(UUID.randomUUID().toString())
				// 커스텀 클레임: 사용자 ID
				.claim(USER_ID, user.getUserId())
				// 커스텀 클레임: 토큰 타입 ("ACCESS" 또는 "REFRESH" 구분)
				.claim(TOKEN_TYPE, TOKEN_TYPE_ACCESS)
				.issuedAt(now)
				.expiration(expiresAt)
				// HMAC SHA256으로 서명 (서명 시 비밀키 사용)
				.signWith(secretKey)
				.compact();
	}

	/**
	 * Access Token 재발급 시 사용하는 Refresh Token생성 (14일)
	 * 
	 * @param user 토큰을 생성할 사용자
	 * @return JWT 형식의 Refresh Token 문자열
	 */
	public String createRefreshToken(User user) {
		Date now = new Date();
		// 현재 시간 + refreshTokenExpirationMs(기본 1209600000ms = 14일)로 만료시간 설정
		Date expiresAt = new Date(now.getTime() + refreshTokenExpirationMs);

		return Jwts.builder()
				.subject(user.getEmail())
				.id(UUID.randomUUID().toString())
				.claim(USER_ID, user.getUserId())
				// 토큰 타입을 "REFRESH"로 설정하여 AccessToken과 구분
				.claim(TOKEN_TYPE, TOKEN_TYPE_REFRESH)
				.issuedAt(now)
				.expiration(expiresAt)
				.signWith(secretKey)
				.compact();
		// 이 토큰은 UserService.login()에서 DB의 refresh_tokens 테이블에 저장되어
		// 토큰 유효성 검증(만료 확인, 폐기 확인)과 토큰 회전(rotation)에 사용됨
	}

	/**
	 * Refresh Token 타입 검증
	 * 
	 * @param token 검증할 Refresh Token
	 * @throws CustomException 토큰 타입이 "REFRESH"가 아닌 경우 INVALID_TOKEN 예외 발생
	 */
	public void validateRefreshToken(String token) {
		Claims claims = getClaims(token);
		if (!TOKEN_TYPE_REFRESH.equals(claims.get(TOKEN_TYPE, String.class))) {  // 토큰 타입이 "REFRESH"가 아니라면 
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * 요청 헤더 Access Token 검증, Spring Security 인증 객체 반환
	 * 
	 * @param token Authorization 헤더에서 "Bearer " 접두사를 제거한 토큰
	 * @return Spring Security Authentication 객체 (이메일이 principal)
	 * @throws CustomException 토큰 검증 실패 시 INVALID_TOKEN 또는 EXPIRED_TOKEN 예외 발생
	 */
	public Authentication getAuthentication(String token) {
		// 토큰 서명 및 만료 시간 검증 (getClaims에서 검증)
		Claims claims = getClaims(token);
		
		if (!TOKEN_TYPE_ACCESS.equals(claims.get(TOKEN_TYPE, String.class))) { // 토큰 타입이 "ACCESS"가 아니라면 
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}

		// 토큰에서 subject(이메일) 추출
		String email = claims.getSubject();
		// JwtAuthenticationFilter에서 Authentication 객체를 SecurityContext에 저장
		// 이후 @AuthenticationPrincipal로 접근 시 email 값이 principal로 사용됨
		return new UsernamePasswordAuthenticationToken(email, null, AuthorityUtils.NO_AUTHORITIES);
	}

	/**
	 * JWT 토큰을 파싱하여 클레임 추출
	 * 토큰 서명, 만료 시간을 검증
	 * 
	 * @param token 파싱할 JWT 토큰
	 * @return 토큰에 포함된 클레임 정보
	 * @throws CustomException 토큰 서명 검증 실패 또는 만료된 경우 예외 발생
	 */
	public Claims getClaims(String token) {
		try {
			return Jwts.parser()
					.verifyWith(secretKey)
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			throw new CustomException(ErrorCode.EXPIRED_TOKEN);
		} catch (RuntimeException e) {
			throw new CustomException(ErrorCode.INVALID_TOKEN);
		}
	}

	/**
	 * Access Token의 만료 시간을 초 단위로 반환
	 * API 응답에 토큰 유효 시간을 포함할 때 사용
	 * 
	 * @return Access Token 만료 시간 (초)
	 */
	public long getAccessTokenExpiresInSeconds() {
		return accessTokenExpirationMs / 1000;
	}

	/**
	 * Refresh Token의 만료 시각을 LocalDateTime으로 반환
	 * RefreshToken의 expiresAt 필드에 저장할 때 사용합
	 * 
	 * @return Refresh Token 만료 시각 (LocalDateTime)
	 */
	public LocalDateTime getRefreshTokenExpiresAt() {
		return LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000L);
	}

	/**
	 * BASE64 시크릿 키 문자열을 바이너리 바이트 배열로 변환
	 * 
	 * @param secret BASE64로 인코딩된 시크릿 키 문자열
	 * @return 바이너리 형식의 바이트 배열
	 */
	private byte[] resolveSecretBytes(String secret) {
		return Decoders.BASE64.decode(secret);
	}
}

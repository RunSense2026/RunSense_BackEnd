package com.example.runsense.global.config;

import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import com.example.runsense.global.security.JwtAuthenticationFilter;
import com.example.runsense.global.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 생성자를 통해 JwtTokenProvider를 주입
	 * @param jwtTokenProvider JWT 토큰 생성 및 검증 담당
	 */
	public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	/**
	 * Spring Security 필터 체인 구성
	 * 
	 * @param http HttpSecurity 빌더
	 * @return 구성된 SecurityFilterChain
	 * @throws Exception 보안 설정 중 발생 가능한 예외
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				// HTTP Basic 인증 비활성화 (JWT 사용)
				.httpBasic(httpBasic -> httpBasic.disable())
				// CSRF 도반 비활성화 (Stateless 기반 API는 CSRF 위험 낮음)
				.csrf(csrf -> csrf.disable())
				// 인증/인가 실패를 공통 JSON 응답으로 처리
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint(authenticationEntryPoint())
						.accessDeniedHandler(accessDeniedHandler()))
				// 세션 비생성 모드 (JWT는 stateless이므로 세션 저장 안 함)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// 로그인, 회원가입, 토큰 재발급, Swagger 문서/UI는 인증 없이 접근 가능
						.requestMatchers("/api/users/signup", "/api/users/email-duplicate", "/api/users/login", "/api/users/reissue", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						// 나머지 모든 요청은 JWT 인증 필수
						.anyRequest().authenticated())
				// UsernamePasswordAuthenticationFilter 전에 JWT 필터 실행 (정식 인증 처리 전 토큰 검증)
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// 토큰 검증 실패 시 응답 처리 (401)
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> writeErrorResponse(response, ErrorCode.ACCESS_DENIED);
	}

	// 권한 부족 시 응답 처리 (403)
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) -> writeErrorResponse(response, ErrorCode.ACCESS_DENIED);
	}

	// 에러 응답 작성 메서드 (401, 403 공통)
	private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		CustomException exception = new CustomException(errorCode);
		response.setStatus(exception.getErrorCode().getStatus().value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(
				exception.getErrorCode().getStatus().value(),
				exception.getErrorCode().getMessage())));
	}
}
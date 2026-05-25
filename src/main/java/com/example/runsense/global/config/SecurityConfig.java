package com.example.runsense.global.config;

import com.example.runsense.global.security.JwtAuthenticationFilter;
import com.example.runsense.global.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
}
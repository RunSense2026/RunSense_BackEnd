package com.example.runsense.global.security;

import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 검증하는 Spring Security 필터
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenProvider jwtTokenProvider;
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * JWT 필터 생성
	 * @param jwtTokenProvider JWT 토큰 검증에 사용할 프로바이더
	 */
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		// 인증이 필요 없는 경로들: 로그인, 회원가입, 토큰 재발급, Swagger 문서/UI
		// 이 경로들은 필터 자체를 실행하지 않음 (성능 최적화)
		return requestUri.startsWith("/api/users/signup")
				|| requestUri.startsWith("/api/users/login")
				|| requestUri.startsWith("/api/users/email-duplicate")
				|| requestUri.startsWith("/api/users/reissue")
				|| requestUri.startsWith("/v3/api-docs")
				|| requestUri.startsWith("/swagger-ui")
				|| requestUri.startsWith("/swagger-ui.html");
	}

	/**
	 * 요청마다 Authorization 헤더의 JWT 토큰을 검증하고 인증 상태를 설정
	 * 
	 * @param request HTTP 요청
	 * @param response HTTP 응답
	 * @param filterChain 필터 체인
	 * @throws ServletException 서블릿 처리 중 예외
	 * @throws IOException I/O 처리 중 예외
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
			try {
				// "Bearer " 접두사 제거 후 JWT 토큰만 추출
				String token = authorizationHeader.substring(BEARER_PREFIX.length());
				// JWT 토큰 검증 후 Authentication 객체 생성 (이메일 기반)
                var auth = jwtTokenProvider.getAuthentication(token);

                if (auth instanceof UsernamePasswordAuthenticationToken) {
                    ((UsernamePasswordAuthenticationToken) auth).setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                }

                SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (CustomException e) {
				// 토큰 검증 실패 -> 에러 응답 후 필터 체인 중단 (다음 필터 실행 불가)
				response.setStatus(e.getErrorCode().getStatus().value());
				response.setContentType("application/json;charset=UTF-8");
                String jsonResponse = objectMapper.writeValueAsString(ApiResponse.fail(e.getErrorCode().getMessage()));
                response.getWriter().write(jsonResponse);
				return; 
			}
		}

		// 토큰 검증 성공하거나, Authorization 헤더가 없거나, Bearer 형식이 아니면 다음 필터로 진행
		filterChain.doFilter(request, response);
	}
}
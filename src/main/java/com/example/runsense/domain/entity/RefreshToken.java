package com.example.runsense.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "refresh_token", unique = true, length = 1000)
	private String refreshToken;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	public static RefreshToken create(User user, String token, LocalDateTime expiresAt) {
		return RefreshToken.builder()
				.user(user)
				.refreshToken(token)
				.expiresAt(expiresAt)
				.build();
	}

	public void issue(String token, LocalDateTime expiresAt) {
		this.refreshToken = token;
		this.expiresAt = expiresAt;
	}

	public void clear() {
		this.refreshToken = null;
		this.expiresAt = null;
	}

	public void revoke() {
		this.revokedAt = LocalDateTime.now();
	}

	public boolean isExpired() {
		return expiresAt == null || expiresAt.isBefore(LocalDateTime.now());
	}
}
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
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false, unique = true, length = 1000)
	private String token;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "revoked_at")
	private LocalDateTime revokedAt;

	public static RefreshToken create(User user, String token, LocalDateTime expiresAt) {
		return RefreshToken.builder()
				.user(user)
				.token(token)
				.expiresAt(expiresAt)
				.createdAt(LocalDateTime.now())
				.build();
	}

	public void rotate(String token, LocalDateTime expiresAt) {
		this.token = token;
		this.expiresAt = expiresAt;
		this.revokedAt = null;
	}

	public boolean isExpired() {
		return expiresAt.isBefore(LocalDateTime.now());
	}

	public boolean isRevoked() {
		return revokedAt != null;
	}
}
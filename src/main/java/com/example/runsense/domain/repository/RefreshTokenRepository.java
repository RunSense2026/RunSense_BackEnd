package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.RefreshToken;
import com.example.runsense.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);

	Optional<RefreshToken> findByUser(User user);
}
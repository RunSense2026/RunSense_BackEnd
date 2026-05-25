package com.example.runsense.domain.service;

import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.runsense.domain.dto.UserResponseDto;
import com.example.runsense.domain.dto.UserSignUpRequestDto;
import com.example.runsense.domain.entity.User;
import com.example.runsense.domain.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponseDto signUp(UserSignUpRequestDto requestDto) {
		// 중복 이메일 여부 확인
		if (userRepository.existsByEmail(requestDto.getEmail())) { // 사용자가 입력한 이메일이 이미 저장되어 있다면
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(requestDto.getLoginPw());

		User user = requestDto.toEntity(encodedPassword); // Builder로 DTO -> 엔티티 변환
		User savedUser = userRepository.save(user); // DB에 저장

		return UserResponseDto.from(savedUser); // 엔티티 -> DTO로 재변환해서 리턴
	}
}
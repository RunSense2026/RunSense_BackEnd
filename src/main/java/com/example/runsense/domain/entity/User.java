package com.example.runsense.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import com.example.runsense.domain.dto.user.UserUpdateRequestDto;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "login_pw", nullable = false)
	private String loginPw;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "gender")
	private String gender;

	@Column(name = "height")
	private Double height;

	@Column(name = "weight")
	private Double weight;

    @Column(name = "resting_heart_rate")
    private Double restingHeartRate;

    @Column(name = "lthr")
    private Integer lthr;

	@Column(name = "is_verified")
	private Boolean isVerified;

	@Column(name = "created_at")
	private LocalDate createdAt;

	public void updateProfile(UserUpdateRequestDto requestDto) {
		if (requestDto.getUserName() != null) {
			this.userName = requestDto.getUserName();
		}
		if (requestDto.getBirthDate() != null) {
			this.birthDate = requestDto.getBirthDate();
		}
		if (requestDto.getGender() != null) {
			this.gender = requestDto.getGender();
		}
		if (requestDto.getHeight() != null) {
			this.height = requestDto.getHeight();
		}
		if (requestDto.getWeight() != null) {
			this.weight = requestDto.getWeight();
		}
	}

	// LTHR 테스트 결과를 현재 LTHR·안정시 심박수로 반영 (유저 정보 수정으로는 바꿀 수 없다)
	public void updateHeartRateProfile(Integer lthr, Double restingHeartRate) {
		this.lthr = lthr;
		this.restingHeartRate = restingHeartRate;
	}
}
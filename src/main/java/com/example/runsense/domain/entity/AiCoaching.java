package com.example.runsense.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ai_coaching")
@Schema(description = "AI 코칭 데이터")
public class AiCoaching {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coaching_id")
	@Schema(description = "코칭 ID", example = "1")
	private Long coachingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	@Schema(description = "연결된 러닝 활동", hidden = true)
	private RunningActivity runningActivity;

	@Column(name = "feedback_text")
	@Schema(description = "피드백 내용", example = "호흡을 가다듬고 페이스를 조금 낮춰보세요.")
	private String feedbackText;

	@Column(name = "focus_point")
	@Schema(description = "분석 중점", example = "호흡 안정과 페이스 조절")
	private String focusPoint;

	@Column(name = "rec_exercise")
	@Schema(description = "추천 운동", example = "가벼운 조깅과 호흡 조절 훈련")
	private String recExercise;
}
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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ai_coaching")
public class AiCoaching {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coaching_id")
	private Long coachingId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private RunningActivity runningActivity;

	@Column(name = "feedback_text")
	private String feedbackText;

	@Column(name = "focus_point")
	private String focusPoint;

	@Column(name = "rec_exercise")
	private String recExercise;
}
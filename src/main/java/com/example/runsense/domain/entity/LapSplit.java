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
@Table(name = "lap_split")
public class LapSplit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "split_id")
	private Long splitId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private RunningActivity runningActivity;

	@Column(name = "lap_number")
	private Long lapNumber;

	@Column(name = "lap_pace")
	private String lapPace;

	@Column(name = "avg_heart_rate")
	private Integer avgHeartRate;

	@Column(name = "pace_statue")
	private String paceStatue;
}
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
@Table(name = "foot_sensor_data")
public class FootSensorData {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sensor_data_id")
	private Long sensorDataId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private RunningActivity runningActivity;

	@Column(name = "foot_side")
	private String footSide;

	@Column(name = "pronation_type")
	private String pronationType;

	@Column(name = "heel_strike_rate")
	private Double heelStrikeRate;

	@Column(name = "hitmap_json")
	private String hitmapJson;
}
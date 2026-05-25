package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.FootSensorData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FootSensorDataRepository extends JpaRepository<FootSensorData, Long> {
	List<FootSensorData> findAllByRunningActivityActivityIdOrderBySensorDataIdAsc(Long activityId);
}
package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.FootSensorData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootSensorDataRepository extends JpaRepository<FootSensorData, Long> {
}
package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.RunningActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.time.LocalDateTime;

public interface RunningActivityRepository extends JpaRepository<RunningActivity, Long> {
	List<RunningActivity> findAllByUserUserIdOrderByActivityDateDesc(Long userId);
	List<RunningActivity> findAllByUserUserIdAndActivityDateBetweenOrderByActivityDateDesc(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
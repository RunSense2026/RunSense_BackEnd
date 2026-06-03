package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.RunningActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RunningActivityRepository extends JpaRepository<RunningActivity, Long> {
	List<RunningActivity> findAllByUserUserIdOrderByActivityDateDesc(Long userId);
}
package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.RunningActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RunningActivityRepository extends JpaRepository<RunningActivity, Long> {
	Page<RunningActivity> findAllByOrderByActivityDateDesc(Pageable pageable);
	Page<RunningActivity> findAllByUserUserIdOrderByActivityDateDesc(Long userId, Pageable pageable);
}
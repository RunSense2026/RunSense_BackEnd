package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.AiCoaching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiCoachingRepository extends JpaRepository<AiCoaching, Long> {
    List<AiCoaching> findAllByRunningActivityActivityIdOrderByCoachingIdAsc(Long activityId);
}
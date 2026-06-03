package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.LapSplit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LapSplitRepository extends JpaRepository<LapSplit, Long> {
    List<LapSplit> findAllByRunningActivityActivityIdOrderBySplitIdAsc(Long activityId);
}
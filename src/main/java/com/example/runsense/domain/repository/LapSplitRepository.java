package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.LapSplit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LapSplitRepository extends JpaRepository<LapSplit, Long> {
}
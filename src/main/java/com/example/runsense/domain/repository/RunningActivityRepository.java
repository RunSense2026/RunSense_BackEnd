package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.RunningActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunningActivityRepository extends JpaRepository<RunningActivity, Long> {
}
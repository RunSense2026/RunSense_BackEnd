package com.example.runsense.domain.repository;

import com.example.runsense.domain.dto.user.UserRunningStatisticsDto;
import com.example.runsense.domain.entity.RunningActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.time.LocalDateTime;

public interface RunningActivityRepository extends JpaRepository<RunningActivity, Long> {
	List<RunningActivity> findAllByUserUserIdOrderByActivityDateDesc(Long userId);
	List<RunningActivity> findAllByUserUserIdAndActivityDateBetweenOrderByActivityDateDesc(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

	@Query("SELECT new com.example.runsense.domain.dto.user.UserRunningStatisticsDto(SUM(ra.distance), COUNT(ra)) " +
			"FROM RunningActivity ra WHERE ra.user.userId = :userId")
    UserRunningStatisticsDto findStatisticsByUserId(@Param("userId") Long userId);
}
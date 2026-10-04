package com.example.runsense.domain.repository;

import com.example.runsense.domain.entity.LthrRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LthrRecordRepository extends JpaRepository<LthrRecord, Long> {
	Optional<LthrRecord> findFirstByUserUserIdOrderByMeasuredAtDescRecordIdDesc(Long userId);
}

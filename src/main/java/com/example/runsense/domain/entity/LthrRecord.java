package com.example.runsense.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// LTHR 테스트 결과 기록. 유저의 LTHR·안정시 심박수는 이 기록으로만 갱신된다.
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lthr_record")
public class LthrRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "lthr", nullable = false)
    private Integer lthr;

    // 직전 테스트 기록의 LTHR (첫 측정이면 null)
    @Column(name = "previous_lthr")
    private Integer previousLthr;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "average_heart_rate")
    private Double averageHeartRate;

    // 테스트 시작 전 안정 측정 단계의 평균 심박수
    @Column(name = "resting_heart_rate")
    private Double restingHeartRate;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;
}

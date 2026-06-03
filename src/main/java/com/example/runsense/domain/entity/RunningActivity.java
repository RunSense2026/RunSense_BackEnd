package com.example.runsense.domain.entity;

import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "running_activity")
public class RunningActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "time")
    private String time;

    @Column(name = "pace")
    private String pace;

    @Column(name = "activity_date")
    private LocalDateTime activityDate;

    @Column(name = "form_score")
    private Short formScore;

    // RunningActivity.java 엔티티 클래스 내부

    public void validateOwner(Long currentUserId) {
        if (!this.user.getUserId().equals(currentUserId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }
}
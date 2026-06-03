package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityDetailResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.dto.running_activity.sub.*;
import com.example.runsense.domain.entity.AiCoaching;
import com.example.runsense.domain.entity.FootSensorData;
import com.example.runsense.domain.entity.LapSplit;
import com.example.runsense.domain.entity.RunningActivity;
import com.example.runsense.domain.entity.User;
import com.example.runsense.domain.repository.AiCoachingRepository;
import com.example.runsense.domain.repository.FootSensorDataRepository;
import com.example.runsense.domain.repository.LapSplitRepository;
import com.example.runsense.domain.repository.RunningActivityRepository;
import com.example.runsense.domain.repository.UserRepository;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RunningActivityService {

    private final RunningActivityRepository runningActivityRepository;
    private final LapSplitRepository lapSplitRepository;
    private final FootSensorDataRepository footSensorDataRepository;
    private final AiCoachingRepository aiCoachingRepository;
    private final UserRepository userRepository;

    public RunningActivityService(RunningActivityRepository runningActivityRepository,
                                  LapSplitRepository lapSplitRepository,
                                  FootSensorDataRepository footSensorDataRepository,
                                  AiCoachingRepository aiCoachingRepository,
                                  UserRepository userRepository) {
        this.runningActivityRepository = runningActivityRepository;
        this.lapSplitRepository = lapSplitRepository;
        this.footSensorDataRepository = footSensorDataRepository;
        this.aiCoachingRepository = aiCoachingRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RunningActivityResponseDto create(RunningActivityCreateRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        RunningActivity runningActivity = RunningActivity.builder()
                .user(user)
                .distance(requestDto.getDistance())
                .time(requestDto.getTime())
                .pace(requestDto.getPace())
                .activityDate(requestDto.getActivityDate())
                .formScore(requestDto.getFormScore())
                .build();
        RunningActivity savedActivity = runningActivityRepository.save(runningActivity);

        List<LapSplit> lapSplits = requestDto.getLapSplits().stream()
                .map(lapSplitRequest -> toLapSplit(savedActivity, lapSplitRequest))
                .toList();
        lapSplitRepository.saveAll(lapSplits);

        List<FootSensorData> footSensorDataList = requestDto.getFootSensorData().stream()
                .map(footSensorRequest -> toFootSensorData(savedActivity, footSensorRequest))
                .toList();
        footSensorDataRepository.saveAll(footSensorDataList);

        List<AiCoaching> aiCoachings = requestDto.getAiCoachings().stream()
                .map(aiCoachingRequest -> toAiCoaching(savedActivity, aiCoachingRequest))
                .toList();
        aiCoachingRepository.saveAll(aiCoachings);

        return RunningActivityResponseDto.builder()
                .activityId(savedActivity.getActivityId())
                .distance(savedActivity.getDistance())
                .time(savedActivity.getTime())
                .pace(savedActivity.getPace())
                .activityDate(savedActivity.getActivityDate())
                .formScore(savedActivity.getFormScore())
                .lapSplitCount(lapSplits.size())
                .footSensorDataCount(footSensorDataList.size())
                .aiCoachingCount(aiCoachings.size())
                .build();
    }

    @Transactional(readOnly = true)
    public RunningActivityDetailResponseDto read(Long activityId, Long currentUserId) { // 1. currentUserId 추가
        RunningActivity runningActivity = runningActivityRepository.findById(activityId)
                .orElseThrow(() -> new CustomException(ErrorCode.RUNNING_ACTIVITY_NOT_FOUND));

        // 본인 확인 검증
        runningActivity.validateOwner(currentUserId);

        List<LapSplitResponseDto> lapSplits = lapSplitRepository.findAllByRunningActivityActivityIdOrderBySplitIdAsc(activityId)
                .stream()
                .map(LapSplitResponseDto::from)
                .toList();

        List<FootSensorDataResponseDto> footSensorData = footSensorDataRepository.findAllByRunningActivityActivityIdOrderBySensorDataIdAsc(activityId)
                .stream()
                .map(FootSensorDataResponseDto::from)
                .toList();

        List<AiCoachingResponseDto> aiCoachings = aiCoachingRepository.findAllByRunningActivityActivityIdOrderByCoachingIdAsc(activityId)
                .stream()
                .map(AiCoachingResponseDto::from)
                .toList();

        return RunningActivityDetailResponseDto.builder()
                .activityId(runningActivity.getActivityId())
                .distance(runningActivity.getDistance())
                .time(runningActivity.getTime())
                .pace(runningActivity.getPace())
                .activityDate(runningActivity.getActivityDate())
                .formScore(runningActivity.getFormScore())
                .lapSplits(lapSplits)
                .footSensorData(footSensorData)
                .aiCoachings(aiCoachings)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RunningActivityResponseDto> readAll(Long currentUserId) {
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        return runningActivityRepository.findAllByUserUserIdOrderByActivityDateDesc(currentUserId)
                .stream()
                .map(this::toRunningActivityResponseDto)
                .toList();
    }

    private RunningActivityResponseDto toRunningActivityResponseDto(RunningActivity runningActivity) {
        int lapSplitCount = lapSplitRepository.findAllByRunningActivityActivityIdOrderBySplitIdAsc(runningActivity.getActivityId()).size();
        int footSensorDataCount = footSensorDataRepository.findAllByRunningActivityActivityIdOrderBySensorDataIdAsc(runningActivity.getActivityId()).size();
        int aiCoachingCount = aiCoachingRepository.findAllByRunningActivityActivityIdOrderByCoachingIdAsc(runningActivity.getActivityId()).size();

        return RunningActivityResponseDto.builder()
                .activityId(runningActivity.getActivityId())
                .distance(runningActivity.getDistance())
                .time(runningActivity.getTime())
                .pace(runningActivity.getPace())
                .activityDate(runningActivity.getActivityDate())
                .formScore(runningActivity.getFormScore())
                .lapSplitCount(lapSplitCount)
                .footSensorDataCount(footSensorDataCount)
                .aiCoachingCount(aiCoachingCount)
                .build();
    }

    private LapSplit toLapSplit(RunningActivity runningActivity, LapSplitCreateRequestDto requestDto) {
        return LapSplit.builder()
                .runningActivity(runningActivity)
                .lapNumber(requestDto.getLapNumber())
                .lapPace(requestDto.getLapPace())
                .avgHeartRate(requestDto.getAvgHeartRate())
                .paceStatue(requestDto.getPaceStatue())
                .build();
    }

    private FootSensorData toFootSensorData(RunningActivity runningActivity, FootSensorDataCreateRequestDto requestDto) {
        return FootSensorData.builder()
                .runningActivity(runningActivity)
                .footSide(requestDto.getFootSide())
                .pronationType(requestDto.getPronationType())
                .heelStrikeRate(requestDto.getHeelStrikeRate())
                .hitmapJson(requestDto.getHitmapJson())
                .build();
    }

    private AiCoaching toAiCoaching(RunningActivity runningActivity, AiCoachingCreateRequestDto requestDto) {
        return AiCoaching.builder()
                .runningActivity(runningActivity)
                .feedbackText(requestDto.getFeedbackText())
                .focusPoint(requestDto.getFocusPoint())
                .recExercise(requestDto.getRecExercise())
                .build();
    }
}
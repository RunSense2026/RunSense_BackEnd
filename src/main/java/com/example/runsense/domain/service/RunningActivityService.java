package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.ai.ReportRequestDto;
import com.example.runsense.domain.dto.ai.ReportResponseDto;
import com.example.runsense.domain.dto.ai.sub.CoachingLogDto;
import com.example.runsense.domain.dto.ai.sub.ReportItemDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityDetailResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityListResponseDto;
import com.example.runsense.domain.dto.running_activity.sub.*;
import com.example.runsense.domain.dto.user.UserRunningStatisticsDto;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class RunningActivityService {

    private final RunningActivityRepository runningActivityRepository;
    private final LapSplitRepository lapSplitRepository;
    private final FootSensorDataRepository footSensorDataRepository;
    private final AiCoachingRepository aiCoachingRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    public RunningActivityService(RunningActivityRepository runningActivityRepository,
                                  LapSplitRepository lapSplitRepository,
                                  FootSensorDataRepository footSensorDataRepository,
                                  AiCoachingRepository aiCoachingRepository,
                                  UserRepository userRepository,
                                  AiService aiService) {
        this.runningActivityRepository = runningActivityRepository;
        this.lapSplitRepository = lapSplitRepository;
        this.footSensorDataRepository = footSensorDataRepository;
        this.aiCoachingRepository = aiCoachingRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
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
                .activityDate(LocalDateTime.now())
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

        // 클라이언트가 보낸 원본 코칭 로그를 그대로 저장하지 않고, AI 요약 리포트로 정리한 결과를 저장한다.
        ReportResponseDto reportSummary = generateReportSummary(requestDto.getAiCoachings());
        List<AiCoaching> aiCoachings = reportSummary == null
                ? List.of() // AI 미호출 시(코칭 로그 없음) 빈 리스트
                : reportSummary.getItems().stream()
                        .map(reportItem -> toAiCoaching(savedActivity, reportItem))
                        .toList();
        aiCoachingRepository.saveAll(aiCoachings);

        return RunningActivityResponseDto.builder()
                .activityId(savedActivity.getActivityId())
                .distance(savedActivity.getDistance())
                .time(savedActivity.getTime())
                .pace(savedActivity.getPace())
                .activityDate(savedActivity.getActivityDate())
                .formScore(savedActivity.getFormScore())
                .aiCoachingSummary(reportSummary == null ? null : SummaryResponseDto.from(reportSummary)) // AI 미호출 시에 null
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

        // 저장 시 AI 요약 리포트 항목으로 정리해 둔 코칭(focusPoint=title, feedbackText=description)
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

    @Transactional
    public Long delete(Long activityId, Long currentUserId){
            RunningActivity runningActivity = runningActivityRepository.findById(activityId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RUNNING_ACTIVITY_NOT_FOUND));

            // 본인 확인
            runningActivity.validateOwner(currentUserId);

            runningActivityRepository.delete(runningActivity);

            return activityId;
    }

    @Transactional(readOnly = true)
    public RunningActivityListResponseDto readAll(Long currentUserId) { // 1. 반환 타입 변경
        // 유저 존재 여부 검증
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // 유저의 전체 러닝 기록 조회 및 DTO 변환
        List<RunningActivityResponseDto> activities = runningActivityRepository.findAllByUserUserIdOrderByActivityDateDesc(currentUserId)
                .stream()
                .map(this::toRunningActivityResponseDto)
                .toList();

        // 통계 데이터 계산
        UserRunningStatisticsDto stats = runningActivityRepository.findStatisticsByUserId(currentUserId);

        // DB 조회 결과가 null일 경우 대비
        int totalCount = stats.getTotalCount() != null ? stats.getTotalCount().intValue() : 0;
        double totalDistance = stats.getTotalDistance() != null ? stats.getTotalDistance() : 0.0;
        double averageFormScore = stats.getAverageFormScore() != null ? stats.getAverageFormScore() : 0.0;

        // 소수점 첫째짜리까지 보이도록
        totalDistance = Math.round(totalDistance * 10) / 10.0;

        return RunningActivityListResponseDto.builder()
                .totalCount(totalCount)
                .totalDistance(totalDistance)
                .averageFormScore(averageFormScore)
                .activities(activities)
                .build();
    }

    @Transactional(readOnly = true)
    public RunningActivityListResponseDto readDate(Long currentUserId, RunningActivityRequestDto requestDto) {
        // 유저 존재 여부 검증
        userRepository.findById(currentUserId).orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        // 날짜가 비어있거나 유효하지 않을 경우(시작 날짜가 종료 날짜보다 이후)
        if (requestDto.getStartDate() == null || requestDto.getEndDate() == null || requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        LocalDateTime startDateTime = requestDto.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = requestDto.getEndDate().atTime(LocalTime.MAX);

        // 유저의 전체 러닝 기록 조회 및 DTO 변환
        List<RunningActivityResponseDto> activities = runningActivityRepository.findAllByUserUserIdAndActivityDateBetweenOrderByActivityDateDesc(
                        currentUserId,
                        startDateTime,
                        endDateTime)
                .stream()
                .map(this::toRunningActivityResponseDto)
                .toList();

        // 통계 데이터 계산
        // 특정 기간 통계 데이터 전용 쿼리 호출
        UserRunningStatisticsDto stats = runningActivityRepository.findStatisticsByUserIdAndDateBetween(currentUserId, startDateTime, endDateTime);

        // DB 조회 결과가 null일 경우 대비
        int totalCount = stats.getTotalCount() != null ? stats.getTotalCount().intValue() : 0;
        double totalDistance = stats.getTotalDistance() != null ? stats.getTotalDistance() : 0.0;
        double averageFormScore = stats.getAverageFormScore() != null ? stats.getAverageFormScore() : 0.0;

        // 소수점 첫째짜리까지 보이도록
        totalDistance = Math.round(totalDistance * 10) / 10.0;
        averageFormScore = Math.round(averageFormScore * 10) / 10.0;

        return RunningActivityListResponseDto.builder()
                .totalCount(totalCount)
                .totalDistance(totalDistance)
                .averageFormScore(averageFormScore)
                .activities(activities)
                .build();
    }

    private RunningActivityResponseDto toRunningActivityResponseDto(RunningActivity runningActivity) {
        return RunningActivityResponseDto.builder()
                .activityId(runningActivity.getActivityId())
                .distance(runningActivity.getDistance())
                .time(runningActivity.getTime())
                .pace(runningActivity.getPace())
                .activityDate(runningActivity.getActivityDate())
                .formScore(runningActivity.getFormScore())
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

    private ReportResponseDto generateReportSummary(List<AiCoachingCreateRequestDto> aiCoachingRequests) {
        if (aiCoachingRequests.isEmpty()) {
            return null; // 요약할 코칭 로그가 없으면 AI를 호출하지 않는다.
        }

        List<CoachingLogDto> coachingLogs = aiCoachingRequests.stream()
                .map(this::toCoachingLogDto)
                .toList();

        ReportRequestDto reportRequestDto = ReportRequestDto.builder()
                .coachingLogs(coachingLogs)
                .build();

        return aiService.createReport(reportRequestDto);
    }

    private CoachingLogDto toCoachingLogDto(AiCoachingCreateRequestDto requestDto) {
        return CoachingLogDto.builder()
                .feedbackText(requestDto.getFeedbackText())
                .focusPoint(requestDto.getFocusPoint())
                .recExercise(requestDto.getRecExercise())
                .build();
    }

    // 리포트 항목(title/description)을 AiCoaching 필드(focusPoint/feedbackText)로 매핑. count는 저장하지 않음.
    private AiCoaching toAiCoaching(RunningActivity runningActivity, ReportItemDto reportItem) {
        return AiCoaching.builder()
                .runningActivity(runningActivity)
                .feedbackText(reportItem.getDescription())
                .focusPoint(reportItem.getTitle())
                .recExercise(reportItem.getRecExercise())
                .build();
    }
}
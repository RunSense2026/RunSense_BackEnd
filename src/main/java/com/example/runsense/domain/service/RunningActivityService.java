package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.running_activity.RunningActivityCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityListResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityListItemResponseDto;
import com.example.runsense.domain.dto.running_activity.RunningActivityResponseDto;
import com.example.runsense.domain.dto.running_activity.detail.AiCoachingReadResponseDto;
import com.example.runsense.domain.dto.running_activity.detail.FootSensorDataReadResponseDto;
import com.example.runsense.domain.dto.running_activity.detail.LapSplitReadResponseDto;
import com.example.runsense.domain.dto.running_activity.detail.RunningActivityDetailResponseDto;
import com.example.runsense.domain.dto.running_activity.sub.AiCoachingCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.sub.FootSensorDataCreateRequestDto;
import com.example.runsense.domain.dto.running_activity.sub.LapSplitCreateRequestDto;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public RunningActivityResponseDto create(RunningActivityCreateRequestDto requestDto) {
		User user = userRepository.findById(requestDto.getUserId())
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
				.userId(user.getUserId())
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
	    public RunningActivityListResponseDto listByUser(Long userId, Pageable pageable) {
		Page<RunningActivity> activities = runningActivityRepository.findAllByUserUserIdOrderByActivityDateDesc(userId, pageable);

		List<RunningActivityListItemResponseDto> content = activities.getContent().stream()
			.map(activity -> RunningActivityListItemResponseDto.builder()
				.activityId(activity.getActivityId())
				.userId(activity.getUser().getUserId())
				.distance(activity.getDistance())
				.time(activity.getTime())
				.pace(activity.getPace())
				.activityDate(activity.getActivityDate())
				.formScore(activity.getFormScore())
				.build())
			.toList();

		return RunningActivityListResponseDto.builder()
			.activities(content)
			.page(activities.getNumber())
			.size(activities.getSize())
			.totalElements(activities.getTotalElements())
			.totalPages(activities.getTotalPages())
			.hasNext(activities.hasNext())
			.build();
	    }

	@Transactional(readOnly = true)
	public RunningActivityListResponseDto listForEmail(String email, Pageable pageable) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));
		return listByUser(user.getUserId(), pageable);
	}

	@Transactional(readOnly = true)
	public RunningActivityDetailResponseDto read(Long activityId) {
		RunningActivity runningActivity = runningActivityRepository.findById(activityId)
				.orElseThrow(() -> new CustomException(ErrorCode.RUNNING_ACTIVITY_NOT_FOUND));

		List<LapSplitReadResponseDto> lapSplits = lapSplitRepository.findAllByRunningActivityActivityIdOrderBySplitIdAsc(activityId)
				.stream()
				.map(LapSplitReadResponseDto::from)
				.toList();

		List<FootSensorDataReadResponseDto> footSensorData = footSensorDataRepository.findAllByRunningActivityActivityIdOrderBySensorDataIdAsc(activityId)
				.stream()
				.map(FootSensorDataReadResponseDto::from)
				.toList();

		List<AiCoachingReadResponseDto> aiCoachings = aiCoachingRepository.findAllByRunningActivityActivityIdOrderByCoachingIdAsc(activityId)
				.stream()
				.map(AiCoachingReadResponseDto::from)
				.toList();

		return RunningActivityDetailResponseDto.builder()
				.activityId(runningActivity.getActivityId())
				.lapSplits(lapSplits)
				.footSensorData(footSensorData)
				.aiCoachings(aiCoachings)
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
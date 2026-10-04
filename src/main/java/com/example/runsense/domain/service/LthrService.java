package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.lthr.LthrRecordCreateRequestDto;
import com.example.runsense.domain.dto.lthr.LthrRecordResponseDto;
import com.example.runsense.domain.entity.LthrRecord;
import com.example.runsense.domain.entity.User;
import com.example.runsense.domain.repository.LthrRecordRepository;
import com.example.runsense.domain.repository.UserRepository;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LthrService {

    private final LthrRecordRepository lthrRecordRepository;
    private final UserRepository userRepository;

    public LthrService(LthrRecordRepository lthrRecordRepository, UserRepository userRepository) {
        this.lthrRecordRepository = lthrRecordRepository;
        this.userRepository = userRepository;
    }

    // 테스트 결과를 기록으로 쌓고, 유저의 현재 LTHR·안정시 심박수도 함께 갱신한다.
    // 직전 LTHR은 클라이언트 값이 아니라 마지막 테스트 기록에서 가져온다.
    @Transactional
    public LthrRecordResponseDto create(LthrRecordCreateRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Integer previousLthr = lthrRecordRepository.findFirstByUserUserIdOrderByMeasuredAtDescRecordIdDesc(userId)
                .map(LthrRecord::getLthr)
                .orElse(null);

        LthrRecord record = lthrRecordRepository.save(LthrRecord.builder()
                .user(user)
                .lthr(requestDto.getLthr())
                .previousLthr(previousLthr)
                .restingHeartRate(requestDto.getRestingHeartRate())
                .maxHeartRate(requestDto.getMaxHeartRate())
                .averageHeartRate(requestDto.getAverageHeartRate())
                .measuredAt(LocalDateTime.now())
                .build());

        user.updateHeartRateProfile(requestDto.getLthr(), requestDto.getRestingHeartRate());

        return LthrRecordResponseDto.from(record, LocalDate.now());
    }

    // 마지막 테스트 기록. 기록이 없으면 null
    @Transactional(readOnly = true)
    public LthrRecordResponseDto readLatest(Long userId) {
        return lthrRecordRepository.findFirstByUserUserIdOrderByMeasuredAtDescRecordIdDesc(userId)
                .map(record -> LthrRecordResponseDto.from(record, LocalDate.now()))
                .orElse(null);
    }
}

package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.lthr.LthrRecordCreateRequestDto;
import com.example.runsense.domain.dto.lthr.LthrRecordResponseDto;
import com.example.runsense.domain.entity.User;
import com.example.runsense.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LthrServiceTest {

    @Autowired
    private LthrService lthrService;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .loginPw("encoded")
                .userName("테스트")
                .email("lthr-test@example.com")
                .isVerified(true)
                .createdAt(LocalDate.now())
                .build());
        userId = user.getUserId();
    }

    private static LthrRecordCreateRequestDto request(int lthr) {
        LthrRecordCreateRequestDto dto = new LthrRecordCreateRequestDto();
        ReflectionTestUtils.setField(dto, "lthr", lthr);
        ReflectionTestUtils.setField(dto, "restingHeartRate", 58.0);
        ReflectionTestUtils.setField(dto, "maxHeartRate", 174);
        ReflectionTestUtils.setField(dto, "averageHeartRate", 158.4);
        return dto;
    }

    @Test
    void 기록이_없으면_마지막_기록은_null이다() {
        assertThat(lthrService.readLatest(userId)).isNull();
    }

    @Test
    void 첫_측정은_직전_LTHR이_없고_유저의_LTHR과_안정시_심박수를_갱신한다() {
        LthrRecordResponseDto saved = lthrService.create(request(158), userId);

        assertThat(saved.getLthr()).isEqualTo(158);
        assertThat(saved.getPreviousLthr()).isNull();
        assertThat(saved.getDaysSinceMeasured()).isZero();
        assertThat(saved.getRecommendRetest()).isFalse();
        assertThat(saved.getRestingHeartRate()).isEqualTo(58.0);
        User user = userRepository.findById(userId).orElseThrow();
        assertThat(user.getLthr()).isEqualTo(158);
        assertThat(user.getRestingHeartRate()).isEqualTo(58.0);
    }

    @Test
    void 이후_측정은_직전_테스트_기록과_비교하고_마지막_기록으로_조회된다() {
        lthrService.create(request(158), userId);
        lthrService.create(request(162), userId);

        LthrRecordResponseDto latest = lthrService.readLatest(userId);

        assertThat(latest.getLthr()).isEqualTo(162);
        assertThat(latest.getPreviousLthr()).isEqualTo(158);
        assertThat(latest.getLthrDifference()).isEqualTo(4);
        assertThat(latest.getMaxHeartRate()).isEqualTo(174);
        assertThat(latest.getAverageHeartRate()).isEqualTo(158.4);
        assertThat(userRepository.findById(userId).orElseThrow().getLthr()).isEqualTo(162);
    }
}

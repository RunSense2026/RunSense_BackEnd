package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.course.ParkListResponseDto;
import com.example.runsense.domain.dto.course.ParkResponseDto;
import com.example.runsense.global.exception.CustomException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseServiceTest {

    // 고양시 향동동 부근
    private static final double LAT = 37.6045;
    private static final double LON = 126.8970;

    private static CourseService courseService;

    @BeforeAll
    static void setUp() {
        courseService = new CourseService(JsonMapper.builder().build());
    }

    @Test
    void 가까운_순서로_기본_3곳을_반환한다() {
        List<ParkResponseDto> parks = courseService.getNearbyParks(LAT, LON, 3, 10000, null).getParks();

        assertThat(parks).hasSize(3);
        assertThat(parks).extracting(ParkResponseDto::getRank).containsExactly(1, 2, 3);
        assertThat(parks).extracting(ParkResponseDto::getDistanceKm).isSorted();
        assertThat(parks).allSatisfy(park -> assertThat(park.getArea()).isGreaterThanOrEqualTo(10000));
        assertThat(parks.get(0).getName()).isEqualTo("향동근린2");
    }

    @Test
    void 필수_태그를_모두_갖춘_공원만_반환한다() {
        ParkListResponseDto result = courseService.getNearbyParks(LAT, LON, 5, 0, List.of("화장실", "주차장"));

        assertThat(result.getParks()).isNotEmpty()
                .allSatisfy(park -> assertThat(park.getTags()).contains("화장실", "주차장"));
    }

    @Test
    void 중복_레코드는_한_번만_나온다() {
        List<ParkResponseDto> parks = courseService.getNearbyParks(35.13, 126.79, 10, 0, null).getParks();

        assertThat(parks).extracting(park -> park.getName() + park.getLatitude() + park.getLongitude())
                .doesNotHaveDuplicates();
    }

    @Test
    void 잘못된_입력은_예외를_던진다() {
        assertThatThrownBy(() -> courseService.getNearbyParks(0, 0, 3, 10000, null))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> courseService.getNearbyParks(LAT, LON, 11, 10000, null))
                .isInstanceOf(CustomException.class);
        assertThatThrownBy(() -> courseService.getNearbyParks(LAT, LON, 3, 10000, List.of("수영장")))
                .isInstanceOf(CustomException.class);
    }
}

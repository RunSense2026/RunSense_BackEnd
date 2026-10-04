package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.course.ParkFacilityDto;
import com.example.runsense.domain.dto.course.ParkListResponseDto;
import com.example.runsense.domain.dto.course.ParkResponseDto;
import com.example.runsense.domain.dto.course.ParkTag;
import com.example.runsense.domain.dto.course.raw.ParkRecordDto;
import com.example.runsense.domain.dto.course.raw.ParkStandardDataFile;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
public class CourseService {

    private static final String PARK_DATA_PATH = "data/parks.json";
    private static final double EARTH_RADIUS_KM = 6371.0;

    // 러닝 코스로 추천하기 부적절한 공원구분
    private static final Set<String> EXCLUDED_PARK_TYPES = Set.of("묘지공원");

    // 한반도 대략적인 위경도 범위 — 벗어나는 원본 좌표(입력 오류)는 로드 시 제외한다.
    private static final double MIN_LAT = 33.0, MAX_LAT = 39.0, MIN_LON = 124.0, MAX_LON = 132.0;

    private final List<Park> parks;

    public CourseService(ObjectMapper objectMapper) {
        this.parks = loadParks(objectMapper);
        log.info("공원 데이터 로드 완료: {}건", parks.size());
    }

    // 현재 위치에서 가까운 공원을 최대 limit개 반환한다.
    // minArea(㎡) 미만이거나 requiredTags를 모두 갖추지 못한 공원은 후보에서 제외한다.
    public ParkListResponseDto getNearbyParks(double lat, double lon, int limit, double minArea, List<String> requiredTags) {
        if (!isInKorea(lat, lon) || limit < 1 || limit > 10 || minArea < 0) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        Set<ParkTag> tagFilter = toTags(requiredTags);

        List<Map.Entry<Park, Double>> nearest = parks.stream()
                .filter(park -> park.area() == null ? minArea == 0 : park.area() >= minArea)
                .filter(park -> park.tags().containsAll(tagFilter))
                .map(park -> Map.entry(park, haversineKm(lat, lon, park.latitude(), park.longitude())))
                .sorted(Map.Entry.comparingByValue())
                .limit(limit)
                .toList();

        List<ParkResponseDto> result = IntStream.range(0, nearest.size())
                .mapToObj(i -> toResponse(i + 1, nearest.get(i).getKey(), nearest.get(i).getValue()))
                .toList();

        return ParkListResponseDto.builder().parks(result).build();
    }

    private Set<ParkTag> toTags(List<String> labels) {
        if (labels == null) {
            return Set.of();
        }
        return labels.stream()
                .filter(label -> !label.isBlank())
                .map(label -> ParkTag.fromLabel(label.trim())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE)))
                .collect(Collectors.toSet());
    }

    private ParkResponseDto toResponse(int rank, Park park, double distanceKm) {
        return ParkResponseDto.builder()
                .rank(rank)
                .name(park.name())
                .type(park.type())
                .address(park.address())
                .latitude(park.latitude())
                .longitude(park.longitude())
                .area(park.area())
                .distanceKm(Math.round(distanceKm * 100) / 100.0)
                .tags(park.tags().stream().map(ParkTag::getLabel).toList())
                .facilities(park.facilities())
                .build();
    }

    private List<Park> loadParks(ObjectMapper objectMapper) {
        ParkStandardDataFile file;
        try (InputStream in = new ClassPathResource(PARK_DATA_PATH).getInputStream()) {
            file = objectMapper.readValue(in, ParkStandardDataFile.class);
        } catch (IOException e) {
            throw new UncheckedIOException("공원 데이터(" + PARK_DATA_PATH + ")를 읽을 수 없습니다.", e);
        }

        // 원본에 같은 공원이 여러 번 들어있는 경우가 있어(관리번호도 고유하지 않음) 공원명+좌표로 중복을 제거한다.
        // 중복이면 데이터기준일자가 최신인 레코드를 남긴다.
        Map<String, ParkRecordDto> deduplicated = new LinkedHashMap<>();
        for (ParkRecordDto record : file.getRecords()) {
            String key = record.getName() + "|" + record.getLatitude() + "|" + record.getLongitude();
            deduplicated.merge(key, record, (prev, next) ->
                    blankToEmpty(next.getBaseDate()).compareTo(blankToEmpty(prev.getBaseDate())) > 0 ? next : prev);
        }

        return deduplicated.values().stream()
                .map(this::toPark)
                .filter(Objects::nonNull)
                .toList();
    }

    // 좌표가 없거나 한반도 밖이면 null (추천 대상에서 제외)
    private Park toPark(ParkRecordDto record) {
        Double lat = parseDouble(record.getLatitude());
        Double lon = parseDouble(record.getLongitude());
        if (lat == null || lon == null || !isInKorea(lat, lon) || EXCLUDED_PARK_TYPES.contains(record.getType())) {
            return null;
        }

        ParkFacilityDto facilities = ParkFacilityDto.builder()
                .sports(blankToNull(record.getSportsFacilities()))
                .play(blankToNull(record.getPlayFacilities()))
                .convenience(blankToNull(record.getConvenienceFacilities()))
                .culture(blankToNull(record.getCultureFacilities()))
                .etc(blankToNull(record.getEtcFacilities()))
                .build();

        String facilityText = Stream.of(facilities.getSports(), facilities.getPlay(), facilities.getConvenience(),
                        facilities.getCulture(), facilities.getEtc())
                .filter(Objects::nonNull)
                .reduce("", (a, b) -> a + " " + b);
        Set<ParkTag> tags = Arrays.stream(ParkTag.values())
                .filter(tag -> tag.matches(facilityText))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(ParkTag.class)));

        Double area = parseDouble(record.getArea());
        String address = blankToNull(record.getRoadAddress()) != null ? record.getRoadAddress().trim() : blankToNull(record.getLotAddress());

        return new Park(record.getName(), record.getType(), address, lat, lon,
                area != null && area > 0 ? area : null, tags, facilities);
    }

    private static boolean isInKorea(double lat, double lon) {
        return lat >= MIN_LAT && lat <= MAX_LAT && lon >= MIN_LON && lon <= MAX_LON;
    }

    // 하버사인 공식 — 두 위경도 사이 직선(대원) 거리(km)
    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * EARTH_RADIUS_KM * Math.asin(Math.sqrt(a));
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value;
    }

    // 로드 후 메모리에 올려두는 정제된 공원 (약 1.7만 건, 요청마다 전체 거리 계산해도 수 ms)
    private record Park(String name, String type, String address, double latitude, double longitude,
                        Double area, Set<ParkTag> tags, ParkFacilityDto facilities) {
    }
}

package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.publicdata.WeatherResponseDto;
import com.example.runsense.domain.dto.publicdata.raw.KmaWeatherApiResponse;
import com.example.runsense.domain.dto.publicdata.raw.KmaWeatherItemDto;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.util.CoordinateConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class PublicDataService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final String kmaApiUrl;
    private final String serviceKey;
    private final RestClient restClient;

    public PublicDataService(
            @Value("${app.public-api.kma-url}") String kmaApiUrl,
            @Value("${app.public-api.service-key}") String serviceKey
    ) {
        this.kmaApiUrl = kmaApiUrl;
        this.serviceKey = serviceKey;
        this.restClient = RestClient.create();
    }

    // 위경도 → 기상청 격자 변환 후, 초단기실황(기온/습도/풍속/강수형태)과 초단기예보(하늘상태)를 조합해 반환한다.
    public WeatherResponseDto getWeather(double lat, double lon) {
        CoordinateConverter.KmaGrid grid = CoordinateConverter.toKmaGrid(lat, lon);

        List<KmaWeatherItemDto> ncstItems = fetchUltraSrtNcst(grid.nx(), grid.ny());
        String skyCondition = fetchSkyCode(grid.nx(), grid.ny());

        return WeatherResponseDto.builder()
                .temperature(findDoubleValue(ncstItems, "T1H"))
                .humidity(findDoubleValue(ncstItems, "REH"))
                .windSpeed(findDoubleValue(ncstItems, "WSD"))
                .precipitationType(findRawValue(ncstItems, "PTY"))
                .skyCondition(skyCondition)
                .build();
    }

    // 기상청 초단기실황(getUltraSrtNcst) 조회. 매시 40분 이후 갱신되므로 40분 이전이면 이전 시간대로 요청한다.
    private List<KmaWeatherItemDto> fetchUltraSrtNcst(int nx, int ny) {
        LocalDateTime now = LocalDateTime.now();
        if (now.getMinute() < 40) now = now.minusHours(1);
        String baseDate = now.format(DATE_FORMAT);
        String baseTime = String.format("%02d00", now.getHour());

        String url = kmaApiUrl + "/getUltraSrtNcst"
                + "?serviceKey=" + serviceKey
                + "&pageNo=1&numOfRows=10&dataType=JSON"
                + "&base_date=" + baseDate + "&base_time=" + baseTime
                + "&nx=" + nx + "&ny=" + ny;

        List<KmaWeatherItemDto> items = requestItems(url);
        if (items == null) throw new CustomException(ErrorCode.PUBLIC_API_ERROR);
        return items;
    }

    // 기상청 초단기예보(getUltraSrtFcst)에서 하늘상태(SKY) 코드만 조회한다.
    // 초단기실황에는 강수형태(PTY)만 있고 구름 정보가 없어, 맑음/구름낌 판별에 필요하다.
    // 매시 30분에 생성되어 45분 이후 제공되므로 45분 이전이면 이전 시간대로 요청한다.
    // 실패해도 날씨 조회 전체를 실패시키지 않고 null 로 폴백한다(아이콘은 PTY만으로 대체 판별).
    private String fetchSkyCode(int nx, int ny) {
        try {
            LocalDateTime now = LocalDateTime.now();
            if (now.getMinute() < 45) now = now.minusHours(1);
            String baseDate = now.format(DATE_FORMAT);
            String baseTime = String.format("%02d30", now.getHour());

            String url = kmaApiUrl + "/getUltraSrtFcst"
                    + "?serviceKey=" + serviceKey
                    + "&pageNo=1&numOfRows=60&dataType=JSON"
                    + "&base_date=" + baseDate + "&base_time=" + baseTime
                    + "&nx=" + nx + "&ny=" + ny;

            List<KmaWeatherItemDto> items = requestItems(url);
            if (items == null) return null;

            // 가장 가까운 예보시각의 SKY 값을 사용한다.
            return items.stream()
                    .filter(item -> "SKY".equals(item.getCategory()))
                    .min(Comparator.comparingInt(item -> parseIntOrMax(item.getFcstTime())))
                    .map(item -> item.getFcstValue() != null ? item.getFcstValue() : item.getObsrValue())
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    // URI.create(url)로 이미 완성된 문자열을 그대로 사용한다.
    // RestClient의 .uri(String) 템플릿 방식은 내부적으로 다시 퍼센트 인코딩을 하기 때문에,
    // 공공데이터포털 발급 serviceKey(이미 인코딩된 값)를 넘기면 이중 인코딩되어 인증 오류가 난다.
    private List<KmaWeatherItemDto> requestItems(String url) {
        try {
            KmaWeatherApiResponse res = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(KmaWeatherApiResponse.class);

            // 응답 구조가 기대와 다르면(장애/키 오류 등) 호출부에서 처리하도록 null 반환
            if (res == null || res.getResponse() == null
                    || res.getResponse().getBody() == null
                    || res.getResponse().getBody().getItems() == null) {
                return null;
            }
            return res.getResponse().getBody().getItems().getItem();
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PUBLIC_API_ERROR);
        }
    }

    // 실황 item 목록에서 category 값을 찾아 숫자로 변환한다 (예: T1H, REH, WSD).
    private Double findDoubleValue(List<KmaWeatherItemDto> items, String category) {
        String raw = findRawValue(items, category);
        if (raw == null) return null;
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 실황 item 목록에서 category 값을 문자열 그대로 찾는다 (예: PTY 코드).
    private String findRawValue(List<KmaWeatherItemDto> items, String category) {
        return items.stream()
                .filter(item -> category.equals(item.getCategory()))
                .findFirst()
                .map(KmaWeatherItemDto::getObsrValue)
                .orElse(null);
    }

    // fcstTime 정렬용 헬퍼. 값이 없거나 숫자가 아니면 정렬 순서상 맨 뒤로 밀어낸다.
    private int parseIntOrMax(String value) {
        if (value == null) return Integer.MAX_VALUE;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
}

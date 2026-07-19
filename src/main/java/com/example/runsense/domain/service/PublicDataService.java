package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.publicdata.AirQualityResponseDto;
import com.example.runsense.domain.dto.publicdata.LocationResponseDto;
import com.example.runsense.domain.dto.publicdata.WeatherResponseDto;
import com.example.runsense.domain.dto.publicdata.raw.AirKoreaAirQualityApiResponse;
import com.example.runsense.domain.dto.publicdata.raw.AirKoreaNearbyStationApiResponse;
import com.example.runsense.domain.dto.publicdata.raw.AirQualityItemDto;
import com.example.runsense.domain.dto.publicdata.raw.KmaWeatherApiResponse;
import com.example.runsense.domain.dto.publicdata.raw.KmaWeatherItemDto;
import com.example.runsense.domain.dto.publicdata.raw.NearbyStationItemDto;
import com.example.runsense.domain.dto.publicdata.raw.NominatimReverseGeocodeResponse;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.util.CoordinateConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PublicDataService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/reverse";
    // Nominatim 사용정책상 식별 가능한 User-Agent가 필요하다.
    private static final String NOMINATIM_USER_AGENT = "RunSense/1.0 (running app)";
    // 시/도명 뒤에 붙는 행정구역 접미사 — 표시용 지역명에서 제거한다 (예: "서울특별시" → "서울").
    private static final Pattern ADMIN_SUFFIX = Pattern.compile("(특별자치시|특별자치도|특별시|광역시)$");

    private final String kmaApiUrl;
    private final String airKoreaApiUrl;
    // 근접측정소 조회는 대기질(ArpltnInforInqireSvc)이 아닌 측정소정보(MsrstnInfoInqireSvc) 서비스 경로를 쓴다.
    private final String msrstnApiUrl;
    private final String serviceKey;
    private final RestClient restClient;
    private final RestClient nominatimRestClient;

    public PublicDataService(
            @Value("${app.public-api.kma-url}") String kmaApiUrl,
            @Value("${app.public-api.air-korea-url}") String airKoreaApiUrl,
            @Value("${app.public-api.service-key}") String serviceKey
    ) {
        this.kmaApiUrl = kmaApiUrl;
        this.airKoreaApiUrl = airKoreaApiUrl;
        this.msrstnApiUrl = airKoreaApiUrl.replace("ArpltnInforInqireSvc", "MsrstnInfoInqireSvc");
        this.serviceKey = serviceKey;
        this.restClient = RestClient.create();

        // Nominatim은 응답이 느릴 수 있어 8초 타임아웃을 둔다.
        SimpleClientHttpRequestFactory nominatimRequestFactory = new SimpleClientHttpRequestFactory();
        nominatimRequestFactory.setConnectTimeout(8000);
        nominatimRequestFactory.setReadTimeout(8000);
        this.nominatimRestClient = RestClient.builder()
                .requestFactory(nominatimRequestFactory)
                .build();
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

    // 위경도 → TM 좌표 변환 후, 최근접 측정소를 찾아 그 측정소의 실시간 측정정보를 반환한다.
    public AirQualityResponseDto getAirQuality(double lat, double lon) {
        CoordinateConverter.TmCoordinate tm = CoordinateConverter.toTm(lat, lon);

        String stationName = fetchNearestStationName(tm.tmX(), tm.tmY());
        if (stationName == null) throw new CustomException(ErrorCode.PUBLIC_API_ERROR);

        AirQualityItemDto item = fetchAirQualityItem(stationName);
        if (item == null) throw new CustomException(ErrorCode.PUBLIC_API_ERROR);

        return AirQualityResponseDto.builder()
                .stationName(stationName)
                .dataTime(item.getDataTime())
                .pm10Value(parseDoubleOrNull(item.getPm10Value()))
                .pm25Value(parseDoubleOrNull(item.getPm25Value()))
                .khaiValue(parseDoubleOrNull(item.getKhaiValue()))
                .pm10Grade(item.getPm10Grade())
                .pm25Grade(item.getPm25Grade())
                .khaiGrade(item.getKhaiGrade())
                .build();
    }

    // 좌표 기반 최근접 대기질 측정소 조회. 자치구명을 그대로 측정소명으로 쓰면 매칭이 안 되는 지역(예: 고양시 덕양구)이 있어
    // TM 좌표로 가장 가까운 실제 측정소명을 찾아 다음 호출(getMsrstnAcctoRltmMesureDnsty)에 사용한다.
    private String fetchNearestStationName(double tmX, double tmY) {
        String url = msrstnApiUrl + "/getNearbyMsrstnList"
                + "?serviceKey=" + serviceKey
                + "&returnType=json&numOfRows=10&pageNo=1"
                + "&tmX=" + tmX + "&tmY=" + tmY + "&ver=1.1";

        try {
            AirKoreaNearbyStationApiResponse res = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(AirKoreaNearbyStationApiResponse.class);

            List<NearbyStationItemDto> items = (res != null && res.getResponse() != null && res.getResponse().getBody() != null)
                    ? res.getResponse().getBody().getItems()
                    : null;
            if (items == null || items.isEmpty()) return null;

            // API가 거리 오름차순으로 주지만, 안전하게 tm(거리) 최소값을 고른다.
            return items.stream()
                    .min(Comparator.comparingDouble(item -> parseDoubleOrMax(item.getTm())))
                    .map(NearbyStationItemDto::getStationName)
                    .filter(name -> name != null && !name.isBlank())
                    .orElse(null);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PUBLIC_API_ERROR);
        }
    }

    // 측정소명으로 실시간 측정정보(PM10/PM2.5/통합대기환경지수)를 조회한다.
    private AirQualityItemDto fetchAirQualityItem(String stationName) {
        String url = airKoreaApiUrl + "/getMsrstnAcctoRltmMesureDnsty"
                + "?serviceKey=" + serviceKey
                + "&returnType=json&numOfRows=1&pageNo=1"
                + "&stationName=" + URLEncoder.encode(stationName, StandardCharsets.UTF_8)
                + "&dataTerm=DAILY";

        try {
            AirKoreaAirQualityApiResponse res = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(AirKoreaAirQualityApiResponse.class);

            List<AirQualityItemDto> items = (res != null && res.getResponse() != null && res.getResponse().getBody() != null)
                    ? res.getResponse().getBody().getItems()
                    : null;
            return (items != null && !items.isEmpty()) ? items.get(0) : null;
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PUBLIC_API_ERROR);
        }
    }

    // 위경도 → 한국 행정구역명 역지오코딩 (OpenStreetMap Nominatim, 키 불필요).
    public LocationResponseDto getLocation(double lat, double lon) {
        String url = NOMINATIM_URL
                + "?format=jsonv2&lat=" + lat + "&lon=" + lon
                + "&accept-language=ko&zoom=14";

        NominatimReverseGeocodeResponse res;
        try {
            res = nominatimRestClient.get()
                    .uri(URI.create(url))
                    .header(HttpHeaders.USER_AGENT, NOMINATIM_USER_AGENT)
                    .retrieve()
                    .body(NominatimReverseGeocodeResponse.class);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.PUBLIC_API_ERROR);
        }

        NominatimReverseGeocodeResponse.Address address = (res != null) ? res.getAddress() : null;
        if (address == null) throw new CustomException(ErrorCode.PUBLIC_API_ERROR);

        // 자치구/시군구명 — 대기질 측정소명 폴백으로도 쓰인다.
        String gu = firstNonBlank(address.getCityDistrict(), address.getBorough(), address.getCounty(),
                address.getCity(), address.getTown());
        gu = (gu != null) ? gu.replaceAll("\\s", "") : "";

        // 시/도명 — 접미사(특별시/광역시 등)를 제거한 표시용 형태.
        String si = stripAdminSuffix(firstNonBlank(address.getCity(), address.getProvince(),
                address.getState(), address.getRegion()));

        String name = joinWithDot(si, gu);
        if (name.isBlank()) {
            String fromDisplayName = firstDisplayNameSegment(res.getDisplayName());
            name = (fromDisplayName != null && !fromDisplayName.isBlank()) ? fromDisplayName : "현재 위치";
        }

        return LocationResponseDto.builder()
                .name(name)
                .gu(gu)
                .build();
    }

    // 시/도명 뒤의 행정구역 접미사를 제거한다. 제거 후 빈 문자열이 되면 원본을 그대로 쓴다.
    private String stripAdminSuffix(String value) {
        String s = (value != null) ? value : "";
        String stripped = ADMIN_SUFFIX.matcher(s).replaceAll("").trim();
        return stripped.isEmpty() ? s : stripped;
    }

    // 표시용 지역명 조합: 비어있지 않은 값들을 " · "로 이어붙인다 (예: "서울 · 강남구").
    private String joinWithDot(String... parts) {
        return Arrays.stream(parts)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.joining(" · "));
    }

    // display_name의 첫 구획(대개 가장 상세한 지명)을 뽑는다. 지역명 조합이 비었을 때의 최종 폴백용.
    private String firstDisplayNameSegment(String displayName) {
        if (displayName == null) return null;
        String[] parts = displayName.split(",");
        return parts.length > 0 ? parts[0].trim() : null;
    }

    // 여러 후보 중 비어있지 않은 첫 값을 반환한다.
    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
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
        return parseDoubleOrNull(findRawValue(items, category));
    }

    // 실황 item 목록에서 category 값을 문자열 그대로 찾는다 (예: PTY 코드).
    private String findRawValue(List<KmaWeatherItemDto> items, String category) {
        return items.stream()
                .filter(item -> category.equals(item.getCategory()))
                .findFirst()
                .map(KmaWeatherItemDto::getObsrValue)
                .orElse(null);
    }

    // 값이 없거나 숫자가 아니면 null.
    private Double parseDoubleOrNull(String value) {
        if (value == null) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // fcstTime/tm(거리) 정렬용 헬퍼. 값이 없거나 숫자가 아니면 정렬 순서상 맨 뒤로 밀어낸다.
    private int parseIntOrMax(String value) {
        if (value == null) return Integer.MAX_VALUE;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private double parseDoubleOrMax(String value) {
        if (value == null) return Double.MAX_VALUE;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }
}

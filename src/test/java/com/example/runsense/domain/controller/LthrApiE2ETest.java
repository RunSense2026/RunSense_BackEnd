package com.example.runsense.domain.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

// 실제 HTTP 서버를 띄워 인증(JWT)·검증·직렬화까지 포함한 LTHR API 흐름을 확인한다. DB는 테스트용 H2.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class LthrApiE2ETest {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private final HttpClient http = HttpClient.newHttpClient();

    @Value("${local.server.port}")
    private int port;

    private record Res(int status, JsonNode body) {
        JsonNode data() {
            return body.get("data");
        }
    }

    private Res call(String method, String path, String token, String json) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .method(method, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json));
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        System.out.printf("[E2E] %s %s -> %d %s%n", method, path, response.statusCode(), response.body());
        JsonNode body = response.body().isBlank() ? JSON.createObjectNode() : JSON.readTree(response.body());
        return new Res(response.statusCode(), body);
    }

    private String signUpAndLogin(String email) throws Exception {
        String credentials = "{\"email\":\"" + email + "\",\"loginPw\":\"password123\"}";
        Res signUp = call("POST", "/api/users/signup", null,
                "{\"email\":\"" + email + "\",\"loginPw\":\"password123\",\"userName\":\"테스터\",\"isVerified\":true}");
        assertThat(signUp.status()).isEqualTo(200);
        Res login = call("POST", "/api/users/login", null, credentials);
        assertThat(login.status()).isEqualTo(200);
        return login.data().get("accessToken").asString();
    }

    @Test
    void LTHR_기록_저장과_조회_전체_흐름() throws Exception {
        // 토큰 없이 호출하면 거부된다 (현재 SecurityConfig 의 인증 실패 응답은 프로젝트 전체가 403)
        assertThat(call("GET", "/api/lthr/latest", null, null).status()).isIn(401, 403);
        assertThat(call("POST", "/api/lthr", null, "{\"lthr\":160,\"restingHeartRate\":58}").status()).isIn(401, 403);

        String token = signUpAndLogin("e2e-lthr@example.com");

        // 기록이 없으면 data null
        Res empty = call("GET", "/api/lthr/latest", token, null);
        assertThat(empty.status()).isEqualTo(200);
        assertThat(empty.data().isNull()).isTrue();

        // 첫 측정
        Res first = call("POST", "/api/lthr", token,
                "{\"lthr\":158,\"restingHeartRate\":61.5,\"maxHeartRate\":174,\"averageHeartRate\":158.4}");
        assertThat(first.status()).isEqualTo(200);
        assertThat(first.data().get("lthr").asInt()).isEqualTo(158);
        assertThat(first.data().get("restingHeartRate").asDouble()).isEqualTo(61.5);
        assertThat(first.data().get("previousLthr").isNull()).isTrue();
        assertThat(first.data().get("lthrDifference").isNull()).isTrue();
        assertThat(first.data().get("maxHeartRate").asInt()).isEqualTo(174);
        assertThat(first.data().get("averageHeartRate").asDouble()).isEqualTo(158.4);
        assertThat(first.data().get("daysSinceMeasured").asLong()).isZero();
        assertThat(first.data().get("recommendRetest").asBoolean()).isFalse();
        assertThat(first.data().get("measuredAt").asString()).matches("\\d{4}-\\d{2}-\\d{2}T.*");

        // 두 번째 측정 — 선택 필드 생략, 직전 테스트 기록과 비교
        Res second = call("POST", "/api/lthr", token, "{\"lthr\":162,\"restingHeartRate\":57}");
        assertThat(second.status()).isEqualTo(200);
        assertThat(second.data().get("previousLthr").asInt()).isEqualTo(158);
        assertThat(second.data().get("lthrDifference").asInt()).isEqualTo(4);
        assertThat(second.data().get("maxHeartRate").isNull()).isTrue();

        // 마지막 기록 조회
        Res latest = call("GET", "/api/lthr/latest", token, null);
        assertThat(latest.data().get("recordId").asLong()).isEqualTo(second.data().get("recordId").asLong());
        assertThat(latest.data().get("lthr").asInt()).isEqualTo(162);

        // 유저 프로필의 LTHR·안정시 심박수도 마지막 테스트 값으로 갱신됨
        Res profile = call("GET", "/api/users", token, null);
        assertThat(profile.data().get("lthr").asInt()).isEqualTo(162);
        assertThat(profile.data().get("restingHeartRate").asDouble()).isEqualTo(57.0);

        // 유저 정보 수정으로는 두 값을 바꿀 수 없다 (다른 필드는 정상 수정)
        Res patched = call("PATCH", "/api/users", token, "{\"weight\":65.5,\"lthr\":170,\"restingHeartRate\":40}");
        assertThat(patched.status()).isEqualTo(200);
        assertThat(patched.data().get("weight").asDouble()).isEqualTo(65.5);
        assertThat(patched.data().get("lthr").asInt()).isEqualTo(162);
        assertThat(patched.data().get("restingHeartRate").asDouble()).isEqualTo(57.0);
        Res third = call("POST", "/api/lthr", token, "{\"lthr\":165,\"restingHeartRate\":56}");
        assertThat(third.data().get("previousLthr").asInt()).isEqualTo(162);

        // 다른 유저에게는 기록이 보이지 않는다
        String otherToken = signUpAndLogin("e2e-lthr-other@example.com");
        assertThat(call("GET", "/api/lthr/latest", otherToken, null).data().isNull()).isTrue();
    }

    @Test
    void 잘못된_입력은_400을_반환하고_저장하지_않는다() throws Exception {
        String token = signUpAndLogin("e2e-lthr-invalid@example.com");

        Res missing = call("POST", "/api/lthr", token, "{}");
        assertThat(missing.status()).isEqualTo(400);
        assertThat(missing.data().get("lthr").asString()).isEqualTo("lthr는 필수 입력 값입니다.");

        assertThat(missing.data().get("restingHeartRate").asString()).isEqualTo("restingHeartRate는 필수 입력 값입니다.");

        Res negative = call("POST", "/api/lthr", token, "{\"lthr\":-5,\"restingHeartRate\":58}");
        assertThat(negative.status()).isEqualTo(400);

        Res noResting = call("POST", "/api/lthr", token, "{\"lthr\":160}");
        assertThat(noResting.status()).isEqualTo(400);

        Res badOptional = call("POST", "/api/lthr", token, "{\"lthr\":160,\"restingHeartRate\":58,\"maxHeartRate\":0}");
        assertThat(badOptional.status()).isEqualTo(400);

        // JSON 파싱 실패는 현재 GlobalExceptionHandler 에 전용 처리가 없어 500 — 저장되지 않는 것만 확인
        Res notJson = call("POST", "/api/lthr", token, "not-json");
        assertThat(notJson.status()).isNotEqualTo(200);

        assertThat(call("GET", "/api/lthr/latest", token, null).data().isNull()).isTrue();
    }
}

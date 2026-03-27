package br.com.example.payroll.integration;

import br.com.example.payroll.domain.PayrollEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Event controller integration tests")
class EventControllerIntegrationTest {

    private static final String COMPANY_ID = "aaaaaaaa-0000-0000-0000-000000000001";
    private static final String EMPLOYEE_ID = "bbbbbbbb-0000-0000-0000-000000000001";
    private static final String BASE_PATH = "/api/v1/events";
    private static final String CONSOLIDATED_PATH = BASE_PATH + "/consolidated";

    private static final String REDIS_IMAGE = "redis:7-alpine";

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    WebTestClient webClient;

    @Autowired
    ReactiveRedisConnectionFactory connectionFactory;

    @Autowired
    ReactiveRedisTemplate<String, PayrollEvent> redisTemplate;

    @BeforeEach
    void cleanRedis() {
        connectionFactory.getReactiveConnection()
                .serverCommands()
                .flushAll()
                .block();
    }

    @Test
    @DisplayName("create event and list returns new entry")
    void create_and_list_event_should_work() {
        Map<String, Object> payload = Map.of(
                "employeeId", EMPLOYEE_ID,
                "eventTypeCode", "BONUS",
                "eventDate", "2026-03-15",
                "amount", 500.0,
                "description", "Performance bonus"
        );

        postEvent(payload)
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.companyId").isEqualTo(COMPANY_ID)
                .jsonPath("$.employeeId").isEqualTo(EMPLOYEE_ID);

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_PATH)
                        .queryParam("employeeId", EMPLOYEE_ID)
                        .queryParam("startDate", "2026-03-01")
                        .queryParam("endDate", "2026-03-31")
                        .build())
                .header("X-Company-Id", COMPANY_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("consolidated endpoint returns correct totals from created events")
    void consolidated_should_return_correct_totals() {
        Map<String, Object> horaExtra = Map.of(
                "employeeId", EMPLOYEE_ID,
                "eventTypeCode", "HORA_EXTRA",
                "eventDate", "2026-03-10",
                "amount", 250.0,
                "quantity", 5.0
        );
        Map<String, Object> falta = Map.of(
                "employeeId", EMPLOYEE_ID,
                "eventTypeCode", "FALTA",
                "eventDate", "2026-03-05",
                "quantity", 1.0,
                "amount", 0.0
        );

        postEvent(horaExtra).expectStatus().isCreated();
        postEvent(falta).expectStatus().isCreated();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CONSOLIDATED_PATH)
                        .queryParam("employeeId", EMPLOYEE_ID)
                        .queryParam("period", "2026-03")
                        .build())
                .header("X-Company-Id", COMPANY_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalHorasExtras").isEqualTo(250.0)
                .jsonPath("$.totalFaltas").isEqualTo(1.0);
    }

    @Test
    @DisplayName("created event key should have a positive TTL in Redis")
    void created_event_should_have_positive_ttl() {
        Map<String, Object> payload = Map.of(
                "employeeId", EMPLOYEE_ID,
                "eventTypeCode", "BONUS",
                "eventDate", "2026-03-15",
                "amount", 100.0
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) postEvent(payload)
                .expectStatus().isCreated()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();
        String eventId = response.get("id").toString();

        String key = "payroll:event:" + EMPLOYEE_ID + ":" + eventId;
        Duration ttl = redisTemplate.getExpire(key).block();

        assertThat(ttl).isNotNull().isPositive();
    }

    @Test
    @DisplayName("request without X-Correlation-Id header should still succeed (filter generates one)")
    void request_without_correlation_id_should_succeed() {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_PATH)
                        .queryParam("employeeId", EMPLOYEE_ID)
                        .queryParam("startDate", "2026-03-01")
                        .queryParam("endDate", "2026-03-31")
                        .build())
                .header("X-Company-Id", COMPANY_ID)
                .exchange()
                .expectStatus().isOk();
    }

    private WebTestClient.ResponseSpec postEvent(Object payload) {
        return webClient.post()
                .uri(BASE_PATH)
                .header("X-Company-Id", COMPANY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .exchange();
    }
}


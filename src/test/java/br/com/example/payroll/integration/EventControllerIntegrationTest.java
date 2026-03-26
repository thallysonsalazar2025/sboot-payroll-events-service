package br.com.example.payroll.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Event controller integration tests")
class EventControllerIntegrationTest {

    private static final String COMPANY_ID = "aaaaaaaa-0000-0000-0000-000000000001";
    private static final String BASE_PATH = "/api/v1/events";
    private static final String CONSOLIDATED_PATH = BASE_PATH + "/consolidated";
    private static final String EMPLOYEE_ID_SEEDED = "bbbbbbbb-0000-0000-0000-000000000001";
    private static final String EMPLOYEE_ID_ONBOARDING = "dddddddd-0000-0000-0000-000000000009";

    @Autowired
    WebTestClient webClient;

    @Test
    @DisplayName("consolidated endpoint returns seeded totals")
    void consolidated_should_return_seeded_values() {
        getWithCompany(uriBuilder -> uriBuilder
                        .path(CONSOLIDATED_PATH)
                        .queryParam("employeeId", EMPLOYEE_ID_SEEDED)
                        .queryParam("period", "2026-03")
                        .build())
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalHorasExtras").isEqualTo(250.0)
                .jsonPath("$.totalFaltas").isEqualTo(1.0);
    }

    @Test
    @DisplayName("create event and list returns new entry")
    void create_and_list_event_should_work() {
        Map<String, Object> payload = Map.of(
                "employeeId", EMPLOYEE_ID_ONBOARDING,
                "eventTypeCode", "BONUS",
                "eventDate", "2026-03-15",
                "amount", 500.0,
                "description", "Performance bonus"
        );

        postEvent(payload)
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists();

        getWithCompany(uriBuilder -> uriBuilder
                        .path(BASE_PATH)
                        .queryParam("employeeId", EMPLOYEE_ID_ONBOARDING)
                        .queryParam("startDate", "2026-03-01")
                        .queryParam("endDate", "2026-03-31")
                        .build())
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }

    private WebTestClient.ResponseSpec postEvent(Object payload) {
        return webClient.post()
                .uri(BASE_PATH)
                .header("X-Company-Id", COMPANY_ID)
                .bodyValue(payload)
                .exchange();
    }

    private WebTestClient.ResponseSpec getWithCompany(Function<UriBuilder, URI> uriBuilder) {
        return webClient.get()
                .uri(uriBuilder)
                .header("X-Company-Id", COMPANY_ID)
                .exchange();
    }
}

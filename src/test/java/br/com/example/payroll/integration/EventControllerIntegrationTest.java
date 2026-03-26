package br.com.example.payroll.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventControllerIntegrationTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void consolidated_should_return_seeded_values() {
        String companyId = "aaaaaaaa-0000-0000-0000-000000000001";
        String employeeId = "bbbbbbbb-0000-0000-0000-000000000001";

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events/consolidated")
                        .queryParam("employeeId", employeeId)
                        .queryParam("period", "2026-03")
                        .build())
                .header("X-Company-Id", companyId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalHorasExtras").isEqualTo(250.0)
                .jsonPath("$.totalFaltas").isEqualTo(1.0);
    }

    @Test
    void create_and_list_event_should_work() {
        String companyId = "aaaaaaaa-0000-0000-0000-000000000001";
        String employeeId = "dddddddd-0000-0000-0000-000000000009";

        var payload = java.util.Map.of(
                "employeeId", employeeId,
                "eventTypeCode", "BONUS",
                "eventDate", "2026-03-15",
                "amount", 500.0,
                "description", "Performance bonus"
        );

        webClient.post()
                .uri("/api/v1/events")
                .header("X-Company-Id", companyId)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/events")
                        .queryParam("employeeId", employeeId)
                        .queryParam("startDate", "2026-03-01")
                        .queryParam("endDate", "2026-03-31")
                        .build())
                .header("X-Company-Id", companyId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class)
                .hasSize(1);
    }
}

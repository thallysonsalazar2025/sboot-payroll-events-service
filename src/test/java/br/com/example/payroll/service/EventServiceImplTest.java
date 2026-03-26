package br.com.example.payroll.service;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.ConsolidatedResponse;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import br.com.example.payroll.repository.PayrollEventRepository;
import br.com.example.payroll.repository.PayrollEventTypeConfigRepository;
import br.com.example.payroll.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EventServiceImplTest {

    @Mock
    PayrollEventRepository eventRepository;

    @Mock
    PayrollEventTypeConfigRepository typeRepository;

    @InjectMocks
    EventServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_shouldPersistAndReturnDto() {
        PayrollEventRequest req = new PayrollEventRequest();
        req.setEmployeeId(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000001"));
        req.setEventTypeCode("HORA_EXTRA");
        req.setEventDate(LocalDate.of(2026,3,10));
        req.setAmount(BigDecimal.valueOf(250));

        PayrollEvent saved = PayrollEvent.builder()
                .id(UUID.randomUUID())
                .companyId(UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001"))
                .employeeId(req.getEmployeeId())
                .eventTypeCode(req.getEventTypeCode())
                .eventDate(req.getEventDate())
                .amount(req.getAmount())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(eventRepository.save(any(PayrollEvent.class))).thenReturn(Mono.just(saved));

        Mono<PayrollEventResponse> result = service.createEvent(saved.getCompanyId(), req);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assert dto.getCompanyId().equals(saved.getCompanyId());
                    assert dto.getEmployeeId().equals(saved.getEmployeeId());
                })
                .verifyComplete();
    }

    @Test
    void consolidate_shouldAggregateCorrectly() {
        UUID companyId = UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001");
        UUID employeeId = UUID.fromString("bbbbbbbb-0000-0000-0000-000000000001");

        PayrollEvent e1 = PayrollEvent.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .employeeId(employeeId)
                .eventTypeCode("HORA_EXTRA")
                .amount(BigDecimal.valueOf(250))
                .eventDate(LocalDate.of(2026,3,10))
                .build();

        PayrollEvent e2 = PayrollEvent.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .employeeId(employeeId)
                .eventTypeCode("FALTA")
                .quantity(BigDecimal.ONE)
                .eventDate(LocalDate.of(2026,3,5))
                .build();

        when(eventRepository.findByEmployeeAndPeriod(any(), any(), any()))
                .thenReturn(Flux.fromIterable(List.of(e1, e2)));

        Mono<ConsolidatedResponse> resp = service.consolidate(companyId, employeeId, java.time.YearMonth.of(2026,3));

        StepVerifier.create(resp)
                .assertNext(c -> {
                    assert c.getTotalHorasExtras().compareTo(BigDecimal.valueOf(250)) == 0;
                    assert c.getTotalFaltas().compareTo(BigDecimal.ONE) == 0;
                })
                .verifyComplete();
    }
}

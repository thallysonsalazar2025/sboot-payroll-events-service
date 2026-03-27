package br.com.example.payroll.service;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.ConsolidatedResponse;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import br.com.example.payroll.repository.PayrollEventRedisRepository;
import br.com.example.payroll.service.impl.EventServiceImpl;
import br.com.example.payroll.service.support.PayrollEventConsolidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceImplTest {

    @Mock
    PayrollEventRedisRepository eventRepository;

    @Mock
    PayrollEventConsolidator consolidator;

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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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

        List<PayrollEvent> events = List.of(e1, e2);
        ConsolidatedResponse expected = new ConsolidatedResponse();

        when(eventRepository.findByEmployeeAndPeriod(any(), any(), any()))
                .thenReturn(Flux.fromIterable(events));
        when(consolidator.consolidate(anyList())).thenReturn(expected);

        StepVerifier.create(service.consolidate(companyId, employeeId, YearMonth.of(2026,3)))
                .expectNext(expected)
                .verifyComplete();

        ArgumentCaptor<List<PayrollEvent>> captor = ArgumentCaptor.forClass(List.class);
        verify(consolidator).consolidate(captor.capture());
        assert captor.getValue().equals(events);
    }
}

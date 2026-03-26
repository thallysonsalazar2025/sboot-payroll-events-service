package br.com.example.payroll.service;

import br.com.example.payroll.dto.ConsolidatedResponse;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.util.UUID;

public interface EventService {
    Mono<PayrollEventResponse> createEvent(UUID companyId, PayrollEventRequest request);
    Flux<PayrollEventResponse> listEvents(UUID companyId, UUID employeeId, String startDate, String endDate);
    Mono<ConsolidatedResponse> consolidate(UUID companyId, UUID employeeId, YearMonth period);
}

package br.com.example.payroll.controller;

import br.com.example.payroll.dto.ConsolidatedResponse;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import br.com.example.payroll.service.EventService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService service;
    private final Logger log = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService service) {
        this.service = service;
    }

    private UUID getCompanyIdFromHeader(String companyHeader) {
        if (companyHeader == null || companyHeader.isBlank()) {
            throw new IllegalArgumentException("X-Company-Id header is required");
        }
        return UUID.fromString(companyHeader);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PayrollEventResponse> create(@RequestHeader("X-Company-Id") String companyHeader,
                                             @Valid @RequestBody PayrollEventRequest request) {
        UUID companyId = getCompanyIdFromHeader(companyHeader);
        log.debug("Create event request for company={}, employee={}", companyId, request.getEmployeeId());
        return service.createEvent(companyId, request);
    }

    @GetMapping
    public Flux<PayrollEventResponse> list(@RequestHeader("X-Company-Id") String companyHeader,
                                           @RequestParam("employeeId") UUID employeeId,
                                           @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
                                           @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate) {
        UUID companyId = getCompanyIdFromHeader(companyHeader);
        log.debug("List events for company={}, employee={}, start={}, end={}", companyId, employeeId, startDate, endDate);
        return service.listEvents(companyId, employeeId, startDate, endDate);
    }

    @GetMapping("/consolidated")
    public Mono<ConsolidatedResponse> consolidated(@RequestHeader("X-Company-Id") String companyHeader,
                                                   @RequestParam("employeeId") UUID employeeId,
                                                   @RequestParam("period") String period) {
        UUID companyId = getCompanyIdFromHeader(companyHeader);
        YearMonth ym = YearMonth.parse(period);
        log.debug("Consolidate events for company={}, employee={}, period={}", companyId, employeeId, period);
        return service.consolidate(companyId, employeeId, ym);
    }
}

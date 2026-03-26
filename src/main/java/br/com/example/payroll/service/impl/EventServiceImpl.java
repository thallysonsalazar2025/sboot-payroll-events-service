package br.com.example.payroll.service.impl;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.ConsolidatedResponse;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import br.com.example.payroll.mapper.EventMapper;
import br.com.example.payroll.repository.PayrollEventRepository;
import br.com.example.payroll.service.EventService;
import br.com.example.payroll.service.support.EventDateRange;
import br.com.example.payroll.service.support.PayrollEventConsolidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.YearMonth;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final PayrollEventRepository eventRepository;
    private final PayrollEventConsolidator consolidator;

    public EventServiceImpl(PayrollEventRepository eventRepository,
                            PayrollEventConsolidator consolidator) {
        this.eventRepository = eventRepository;
        this.consolidator = consolidator;
    }

    @Override
    public Mono<PayrollEventResponse> createEvent(UUID companyId, PayrollEventRequest request) {
        PayrollEvent entity = EventMapper.toEntity(request, companyId);
        return eventRepository.save(entity)
                .doOnSuccess(e -> log.debug("Created payroll event {} for company {}", e.getId(), companyId))
                .map(EventMapper::toDto);
    }

    @Override
    public reactor.core.publisher.Flux<PayrollEventResponse> listEvents(UUID companyId, UUID employeeId, String startDate, String endDate) {
        EventDateRange range = EventDateRange.fromStrings(startDate, endDate);
        return eventsForCompany(companyId, employeeId, range)
                .map(EventMapper::toDto);
    }

    @Override
    public Mono<ConsolidatedResponse> consolidate(UUID companyId, UUID employeeId, YearMonth period) {
        EventDateRange range = EventDateRange.fromYearMonth(period);
        return eventsForCompany(companyId, employeeId, range)
                .collectList()
                .map(consolidator::consolidate);
    }

    private Flux<PayrollEvent> eventsForCompany(UUID companyId, UUID employeeId, EventDateRange range) {
        return eventRepository.findByEmployeeAndPeriod(employeeId, range.start(), range.end())
                .filter(event -> companyId.equals(event.getCompanyId()));
    }
}

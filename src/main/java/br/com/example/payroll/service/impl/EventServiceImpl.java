package br.com.example.payroll.service.impl;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.ConsolidatedResponse;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import br.com.example.payroll.mapper.EventMapper;
import br.com.example.payroll.repository.PayrollEventRepository;
import br.com.example.payroll.repository.PayrollEventTypeConfigRepository;
import br.com.example.payroll.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final PayrollEventRepository eventRepository;
    private final PayrollEventTypeConfigRepository typeRepository;

    public EventServiceImpl(PayrollEventRepository eventRepository,
                            PayrollEventTypeConfigRepository typeRepository) {
        this.eventRepository = eventRepository;
        this.typeRepository = typeRepository;
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
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return eventRepository.findByEmployeeAndPeriod(employeeId, start, end)
                .filter(e -> e.getCompanyId() != null && e.getCompanyId().equals(companyId))
                .map(EventMapper::toDto);
    }

    @Override
    public Mono<ConsolidatedResponse> consolidate(UUID companyId, UUID employeeId, YearMonth period) {
        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();
        return eventRepository.findByEmployeeAndPeriod(employeeId, start, end)
                .filter(e -> e.getCompanyId() != null && e.getCompanyId().equals(companyId))
                .collectList()
                .flatMap(list -> Mono.just(doConsolidation(list)));
    }

    private ConsolidatedResponse doConsolidation(List<PayrollEvent> list) {
        BigDecimal totalHorasExtras = BigDecimal.ZERO;
        BigDecimal totalFaltas = BigDecimal.ZERO;
        BigDecimal totalDescontos = BigDecimal.ZERO;
        BigDecimal totalBeneficios = BigDecimal.ZERO;

        for (PayrollEvent e : list) {
            String code = e.getEventTypeCode();
            BigDecimal amount = e.getAmount() == null ? BigDecimal.ZERO : e.getAmount();
            BigDecimal qty = e.getQuantity() == null ? BigDecimal.ZERO : e.getQuantity();

            switch (code) {
                case "HORA_EXTRA" -> totalHorasExtras = totalHorasExtras.add(amount);
                case "FALTA" -> totalFaltas = totalFaltas.add(qty);
                case "ADIANTAMENTO", "PENSION" -> totalDescontos = totalDescontos.add(amount);
                case "BENEF_VR", "BENEF_VA", "BONUS" -> totalBeneficios = totalBeneficios.add(amount);
                default -> {
                    // not categorized
                }
            }
        }

        ConsolidatedResponse resp = new ConsolidatedResponse();
        resp.setTotalHorasExtras(totalHorasExtras);
        resp.setTotalFaltas(totalFaltas);
        resp.setTotalDescontos(totalDescontos);
        resp.setTotalBeneficios(totalBeneficios);
        return resp;
    }
}

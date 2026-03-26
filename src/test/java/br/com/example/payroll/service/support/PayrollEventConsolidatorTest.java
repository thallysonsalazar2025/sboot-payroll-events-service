package br.com.example.payroll.service.support;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.ConsolidatedResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayrollEventConsolidatorTest {

    @Test
    void consolidate_sumsKnownCategories() {
        PayrollEventConsolidator consolidator = new PayrollEventConsolidator();

        List<PayrollEvent> events = List.of(
                PayrollEvent.builder()
                        .id(UUID.randomUUID())
                        .eventTypeCode("HORA_EXTRA")
                        .amount(BigDecimal.valueOf(250))
                        .build(),
                PayrollEvent.builder()
                        .id(UUID.randomUUID())
                        .eventTypeCode("FALTA")
                        .quantity(BigDecimal.ONE)
                        .build(),
                PayrollEvent.builder()
                        .id(UUID.randomUUID())
                        .eventTypeCode("BONUS")
                        .amount(BigDecimal.valueOf(500))
                        .build()
        );

        ConsolidatedResponse result = consolidator.consolidate(events);

        assertEquals(BigDecimal.valueOf(250), result.getTotalHorasExtras());
        assertEquals(BigDecimal.ONE, result.getTotalFaltas());
        assertEquals(BigDecimal.ZERO, result.getTotalDescontos());
        assertEquals(BigDecimal.valueOf(500), result.getTotalBeneficios());
    }
}

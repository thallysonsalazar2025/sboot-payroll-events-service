package br.com.example.payroll.mapper;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventMapperTest {

    @Test
    void toEntity_and_toDto_should_work() {
        PayrollEventRequest req = new PayrollEventRequest();
        req.setEmployeeId(UUID.fromString("bbbbbbbb-0000-0000-0000-000000000001"));
        req.setEventTypeCode("HORA_EXTRA");
        req.setEventDate(LocalDate.of(2026, 3, 10));
        req.setQuantity(BigDecimal.valueOf(5));
        req.setAmount(BigDecimal.valueOf(250));
        req.setDescription("Hora extra noturna 50%");

        PayrollEvent entity = EventMapper.toEntity(req, UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001"));

        assertThat(entity).isNotNull();
        assertThat(entity.getCompanyId()).isEqualTo(UUID.fromString("aaaaaaaa-0000-0000-0000-000000000001"));
        assertThat(entity.getEmployeeId()).isEqualTo(req.getEmployeeId());
        assertThat(entity.getEventTypeCode()).isEqualTo("HORA_EXTRA");

        PayrollEventResponse dto = EventMapper.toDto(entity);
        assertThat(dto).isNotNull();
        assertThat(dto.getCompanyId()).isEqualTo(entity.getCompanyId());
        assertThat(dto.getEmployeeId()).isEqualTo(entity.getEmployeeId());
    }
}

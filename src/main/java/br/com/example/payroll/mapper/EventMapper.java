package br.com.example.payroll.mapper;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.PayrollEventRequest;
import br.com.example.payroll.dto.PayrollEventResponse;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventMapper {

    public static PayrollEvent toEntity(PayrollEventRequest req, UUID companyId) {
        PayrollEvent entity = PayrollEvent.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .employeeId(req.getEmployeeId())
                .eventTypeCode(req.getEventTypeCode())
                .description(req.getDescription())
                .eventDate(req.getEventDate())
                .quantity(req.getQuantity())
                .amount(req.getAmount())
                .metadata(req.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entity.setNewEntity(true);
        return entity;
    }

    public static PayrollEventResponse toDto(PayrollEvent e) {
        PayrollEventResponse r = new PayrollEventResponse();
        r.setId(e.getId());
        r.setCompanyId(e.getCompanyId());
        r.setEmployeeId(e.getEmployeeId());
        r.setEventTypeCode(e.getEventTypeCode());
        r.setDescription(e.getDescription());
        r.setEventDate(e.getEventDate());
        r.setQuantity(e.getQuantity());
        r.setAmount(e.getAmount());
        r.setMetadata(e.getMetadata());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }
}

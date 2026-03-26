package br.com.example.payroll.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PayrollEventResponse {
    private UUID id;
    private UUID companyId;
    private UUID employeeId;
    private String eventTypeCode;
    private String description;
    private LocalDate eventDate;
    private BigDecimal quantity;
    private BigDecimal amount;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

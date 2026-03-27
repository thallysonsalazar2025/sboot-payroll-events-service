package br.com.example.payroll.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollEvent {
    private UUID id;
    private UUID companyId;
    private UUID employeeId;
    private String eventTypeCode;
    private String description;
    private LocalDate eventDate;
    private BigDecimal quantity;
    private BigDecimal amount;
    private String metadata; // JSON as String
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package br.com.example.payroll.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("payroll_event")
public class PayrollEvent {
    @Id
    private UUID id;
    private UUID companyId;
    private UUID employeeId;
    private String eventTypeCode;
    private String description;
    private LocalDate eventDate;
    private BigDecimal quantity;
    private BigDecimal amount;
    private String metadata; // JSON as String
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

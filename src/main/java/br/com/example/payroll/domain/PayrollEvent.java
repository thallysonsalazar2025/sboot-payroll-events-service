package br.com.example.payroll.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("payroll_event")
public class PayrollEvent implements Persistable<UUID> {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return newEntity;
    }
}

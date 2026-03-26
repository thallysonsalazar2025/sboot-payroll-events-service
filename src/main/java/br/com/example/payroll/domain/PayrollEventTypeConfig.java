package br.com.example.payroll.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("payroll_event_type_config")
public class PayrollEventTypeConfig {
    @Id
    private UUID id;
    private String code;
    private String description;
    private String category; // PROVENTO / DESCONTO / INFORMATIVO
    private Boolean impactsSalary;
    private Boolean isDiscount;
    private String calculationHint; // FIXED / MULTIPLIER / PERCENTAGE
    private Boolean active;
    private LocalDateTime createdAt;
}

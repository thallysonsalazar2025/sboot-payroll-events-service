package br.com.example.payroll.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayrollEventTypeConfig {
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

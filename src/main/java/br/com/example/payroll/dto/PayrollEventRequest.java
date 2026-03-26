package br.com.example.payroll.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PayrollEventRequest {

    @NotNull
    private UUID employeeId;

    @NotBlank
    private String eventTypeCode;

    private String description;

    @NotNull
    private LocalDate eventDate;

    private BigDecimal quantity;

    private BigDecimal amount;

    private String metadata;
}

package br.com.example.payroll.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConsolidatedResponse {
    private BigDecimal totalHorasExtras = BigDecimal.ZERO;
    private BigDecimal totalFaltas = BigDecimal.ZERO;
    private BigDecimal totalDescontos = BigDecimal.ZERO;
    private BigDecimal totalBeneficios = BigDecimal.ZERO;
}

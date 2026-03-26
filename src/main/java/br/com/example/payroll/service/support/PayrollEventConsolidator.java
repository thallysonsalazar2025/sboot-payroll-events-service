package br.com.example.payroll.service.support;

import br.com.example.payroll.domain.PayrollEvent;
import br.com.example.payroll.dto.ConsolidatedResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PayrollEventConsolidator {

    public ConsolidatedResponse consolidate(List<PayrollEvent> events) {
        BigDecimal totalHorasExtras = BigDecimal.ZERO;
        BigDecimal totalFaltas = BigDecimal.ZERO;
        BigDecimal totalDescontos = BigDecimal.ZERO;
        BigDecimal totalBeneficios = BigDecimal.ZERO;

        for (PayrollEvent event : events) {
            String code = event.getEventTypeCode();
            BigDecimal amount = safe(event.getAmount());
            BigDecimal quantity = safe(event.getQuantity());

            switch (code) {
                case "HORA_EXTRA" -> totalHorasExtras = totalHorasExtras.add(amount);
                case "FALTA" -> totalFaltas = totalFaltas.add(quantity);
                case "ADIANTAMENTO", "PENSION" -> totalDescontos = totalDescontos.add(amount);
                case "BENEF_VR", "BENEF_VA", "BONUS" -> totalBeneficios = totalBeneficios.add(amount);
                default -> {
                    // not categorized
                }
            }
        }

        ConsolidatedResponse response = new ConsolidatedResponse();
        response.setTotalHorasExtras(totalHorasExtras);
        response.setTotalFaltas(totalFaltas);
        response.setTotalDescontos(totalDescontos);
        response.setTotalBeneficios(totalBeneficios);
        return response;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}

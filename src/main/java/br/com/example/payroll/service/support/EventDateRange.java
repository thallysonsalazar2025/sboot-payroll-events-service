package br.com.example.payroll.service.support;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Simple value object that represents a closed date range for payroll events.
 */
public record EventDateRange(LocalDate start, LocalDate end) {

    public static EventDateRange fromStrings(String startDate, String endDate) {
        Objects.requireNonNull(startDate, "startDate is required");
        Objects.requireNonNull(endDate, "endDate is required");
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return new EventDateRange(start, end);
    }

    public static EventDateRange fromYearMonth(YearMonth period) {
        Objects.requireNonNull(period, "period is required");
        return new EventDateRange(period.atDay(1), period.atEndOfMonth());
    }
}

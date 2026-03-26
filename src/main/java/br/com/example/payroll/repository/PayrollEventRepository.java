package br.com.example.payroll.repository;

import br.com.example.payroll.domain.PayrollEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.UUID;

public interface PayrollEventRepository extends ReactiveCrudRepository<PayrollEvent, UUID> {

    @Query("SELECT * FROM payroll_event WHERE employee_id = :employeeId AND event_date >= :startDate AND event_date <= :endDate")
    Flux<PayrollEvent> findByEmployeeAndPeriod(UUID employeeId, LocalDate startDate, LocalDate endDate);
}

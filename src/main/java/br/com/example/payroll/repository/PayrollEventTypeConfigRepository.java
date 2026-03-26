package br.com.example.payroll.repository;

import br.com.example.payroll.domain.PayrollEventTypeConfig;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface PayrollEventTypeConfigRepository extends ReactiveCrudRepository<PayrollEventTypeConfig, UUID> {

    @Query("SELECT * FROM payroll_event_type_config WHERE active = true")
    Flux<PayrollEventTypeConfig> findAllActive();
}

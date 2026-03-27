package br.com.example.payroll.repository;

import br.com.example.payroll.domain.PayrollEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Redis-backed repository for payroll events.
 * Events are stored as JSON strings under keys of the form:
 *   payroll:event:{employeeId}:{id}
 * Each key carries a configurable TTL (default 30 days).
 */
@Component
public class PayrollEventRedisRepository {

    static final String KEY_PREFIX = "payroll:event:";

    private final ReactiveRedisTemplate<String, PayrollEvent> redisTemplate;
    private final Duration ttl;

    public PayrollEventRedisRepository(
            ReactiveRedisTemplate<String, PayrollEvent> redisTemplate,
            @Value("${payroll.events.ttl-days:30}") long ttlDays) {
        this.redisTemplate = redisTemplate;
        this.ttl = Duration.ofDays(ttlDays);
    }

    public String buildKey(UUID employeeId, UUID id) {
        return KEY_PREFIX + employeeId + ":" + id;
    }

    public Mono<PayrollEvent> save(PayrollEvent event) {
        String key = buildKey(event.getEmployeeId(), event.getId());
        return redisTemplate.opsForValue()
                .set(key, event, ttl)
                .thenReturn(event);
    }

    public Flux<PayrollEvent> findByEmployeeAndPeriod(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        String pattern = KEY_PREFIX + employeeId + ":*";
        return redisTemplate.scan(ScanOptions.scanOptions().match(pattern).count(100).build())
                .collectList()
                .flatMapMany(keys -> keys.isEmpty()
                        ? Flux.empty()
                        : redisTemplate.opsForValue().multiGet(keys).flatMapMany(Flux::fromIterable))
                .filter(event -> event != null
                        && !event.getEventDate().isBefore(startDate)
                        && !event.getEventDate().isAfter(endDate));
    }
}

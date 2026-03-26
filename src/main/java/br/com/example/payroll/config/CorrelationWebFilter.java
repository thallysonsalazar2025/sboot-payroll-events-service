package br.com.example.payroll.config;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * WebFilter que garante X-Correlation-Id e popula MDC para logs.
 * Mantém comportamento reativo.
 */
@Component
public class CorrelationWebFilter implements WebFilter {

    private static final String CORRELATION_HEADER = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String incoming = request.getHeaders().getFirst(CORRELATION_HEADER);
        String correlationId = (incoming == null || incoming.isBlank()) ? UUID.randomUUID().toString() : incoming;

        // Put correlationId into MDC for this reactive flow
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(MDC_KEY, correlationId))
                .doOnEach(signal -> {
                    // For each signal, if context has correlationId, push into MDC so Logback sees it
                    if (signal.getContextView().hasKey(MDC_KEY)) {
                        MDC.put(MDC_KEY, signal.getContextView().get(MDC_KEY));
                    }
                })
                .doFinally(sig -> MDC.remove(MDC_KEY));
    }
}

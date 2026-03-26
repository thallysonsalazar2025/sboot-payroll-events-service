package br.com.example.payroll.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Global error handler for WebFlux
 */
@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(org.springframework.web.server.ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        int status = 500;

        if (ex instanceof ResponseStatusException) {
            status = ((ResponseStatusException) ex).getStatusCode().value();
        } else if (ex instanceof IllegalArgumentException) {
            status = 400;
        }

        response.setStatusCode(org.springframework.http.HttpStatus.valueOf(status));

        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getClass().getSimpleName());
        error.put("message", ex.getMessage());

        try {
            byte[] bytes = mapper.writeValueAsString(error).getBytes(StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            log.error("Failed to write error response", e);
            return Mono.empty();
        }
    }
}

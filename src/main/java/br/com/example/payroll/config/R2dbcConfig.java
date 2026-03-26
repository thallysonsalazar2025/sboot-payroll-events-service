package br.com.example.payroll.config;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

/**
 * Configuração simples para R2DBC H2. Spring Boot autoconfigura a partir de spring.r2dbc.url,
 * mas deixamos o DatabaseClient como bean utilitário.
 */
@Configuration
public class R2dbcConfig {

    @Bean
    public DatabaseClient databaseClient() {
        H2ConnectionConfiguration conf = H2ConnectionConfiguration.builder()
                .inMemory("payrolldb")
                .property("DB_CLOSE_DELAY", "-1")
                .build();
        H2ConnectionFactory factory = new H2ConnectionFactory(conf);
        return DatabaseClient.create(factory);
    }
}

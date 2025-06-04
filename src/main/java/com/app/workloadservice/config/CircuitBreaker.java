package com.app.workloadservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class CircuitBreaker {
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig  config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .slidingWindowSize(10)
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public io.github.resilience4j.circuitbreaker.CircuitBreaker mainServiceCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("mainService");
    }
}

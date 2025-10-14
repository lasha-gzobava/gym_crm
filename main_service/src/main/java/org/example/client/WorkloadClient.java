package org.example.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.training.TrainingEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadClient {

    private final WebClient webClient;
    private final CircuitBreakerRegistry cbRegistry;

    @Value("${workload.client.timeout:3s}")
    private Duration timeout;

    public boolean sendEvent(TrainingEventRequest event, String jwtToken) {
        CircuitBreaker cb = cbRegistry.circuitBreaker("workloadService");

        Supplier<Boolean> call = () -> {
            log.info("Sending event to workload service...");
            webClient.post()
                    .uri("/workloads")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(event)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(timeout)
                    .block();
            log.info("Event sent successfully to workload service.");
            return true;
        };

        Supplier<Boolean> decorated = Decorators.ofSupplier(call)
                .withCircuitBreaker(cb)
                .withFallback(throwable -> {
                    if (throwable instanceof WebClientResponseException ex) {
                        log.error("Workload service returned HTTP {}: {}", ex.getStatusCode(), ex.getMessage());
                    } else if (throwable instanceof WebClientRequestException ex) {
                        log.error("Connection failure: {}", ex.getMessage());
                    } else {
                        log.error("Circuit breaker triggered or workload service unavailable: {}", throwable.getMessage());
                    }
                    return false;
                })
                .decorate();

        return decorated.get();
    }


    @PostConstruct
    public void initCircuitBreakerLogging() {
        CircuitBreaker cb = cbRegistry.circuitBreaker("workloadService");

        cb.getEventPublisher()
                .onStateTransition(event -> log.warn(">>> CircuitBreaker '{}' changed state from {} to {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onFailureRateExceeded(event -> log.warn(">>> Failure rate {}% exceeded for CircuitBreaker '{}'",
                        event.getFailureRate(), event.getCircuitBreakerName()))
                .onError(event -> log.warn(">>> Recorded error for CircuitBreaker '{}': {}",
                        event.getCircuitBreakerName(), event.getThrowable().toString()));
    }

}

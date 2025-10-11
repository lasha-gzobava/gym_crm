package org.example.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.training.TrainingEventRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadClient {

    private final WebClient webClient;
    private final CircuitBreakerRegistry cbRegistry;

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Default timeout for the call
    @Value("${workload.client.timeout:3s}")
    private Duration timeout;

    public void sendEvent(TrainingEventRequest event, String jwtToken) {
        CircuitBreaker cb = cbRegistry.circuitBreaker("workloadService");

        Runnable task = () -> {
            log.info("Sending event to workload service...");
            try {
                webClient.post()
                        .uri("/workloads")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(event)
                        .retrieve()
                        .toBodilessEntity()
                        .timeout(timeout) // ⏰ enforce network timeout
                        .onErrorResume(throwable -> {
                            if (throwable instanceof WebClientResponseException ex) {
                                log.error(" Workload service returned HTTP {}: {}", ex.getStatusCode(), ex.getMessage());
                            } else if (throwable instanceof java.util.concurrent.TimeoutException) {
                                log.error("️ Timeout while calling workload service after {}", timeout);
                            } else {
                                log.error(" Unexpected error calling workload service: {}", throwable.getMessage());
                            }
                            return Mono.empty();
                        })
                        .block();
                log.info(" Event sent successfully to workload service.");
            } catch (Exception e) {
                log.error(" Circuit breaker triggered or workload service unavailable: {}", e.getMessage());
            }
        };

        Runnable decorated = Decorators.ofRunnable(task)
                .withCircuitBreaker(cb)
                .decorate();

        try {
            decorated.run();
        } catch (Exception ex) {
            log.error(" Final fallback after circuit breaker failure: {}", ex.getMessage());
        }
    }
}

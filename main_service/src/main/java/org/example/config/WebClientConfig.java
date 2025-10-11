package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8082")
                .filter((request, next) -> {
                    log.info(" {} {}", request.method(), request.url());
                    return next.exchange(request)
                            .doOnNext(response -> log.info("Response: {}", response.statusCode()));
                })
                .build();
    }
}

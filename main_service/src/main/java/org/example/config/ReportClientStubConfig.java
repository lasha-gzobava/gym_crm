package org.example.config;

import lombok.extern.slf4j.Slf4j;
import org.example.client.ReportClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ReportClientStubConfig {

    @Bean
    @ConditionalOnMissingBean(ReportClient.class)
    public ReportClient reportClientStub() {
        return username -> log.info("[MOCK] Skipping report deletion for trainee '{}'. Report service not available.", username);
    }
}

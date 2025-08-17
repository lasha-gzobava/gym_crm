package org.example.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component("externalApi")
@ConditionalOnProperty(name = "health.external-api.url") // only registers when property is present
public class ExternalApiHealth extends AbstractHealthIndicator {
    private static final Logger log = LoggerFactory.getLogger(ExternalApiHealth.class);

    private final RestClient client;
    private final String url;

    public ExternalApiHealth(@Value("${health.external-api.url}") String url) {
        this.url = url;

        var rf = new SimpleClientHttpRequestFactory();
        // If your IDE complains, use milliseconds: 1000 / 2000
        rf.setConnectTimeout(Duration.ofSeconds(1));
        rf.setReadTimeout(Duration.ofSeconds(2));

        this.client = RestClient.builder()
                .requestFactory(rf)  // or .requestFactory(() -> rf)
                .build();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {
            var resp = client.get().uri(url).retrieve().toBodilessEntity();
            HttpStatusCode code = resp.getStatusCode();
            if (code.is2xxSuccessful()) {
                builder.up().withDetail("url", url).withDetail("status", code.value());
            } else {
                builder.down().withDetail("url", url).withDetail("status", code.value());
            }
        } catch (Exception ex) {
            // keep logs quiet in dev; details still appear in /actuator/health
            log.debug("externalApi health failed: {}", ex.toString());
            builder.down()
                    .withDetail("url", url)
                    .withDetail("error", ex.getClass().getSimpleName())
                    .withDetail("message", ex.getMessage());
        }
    }
}

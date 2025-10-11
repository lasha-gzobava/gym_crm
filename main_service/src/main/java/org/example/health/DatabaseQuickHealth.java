package org.example.health;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("databaseQuick")
public class DatabaseQuickHealth implements HealthIndicator {

    private final JdbcTemplate jdbc;

    @Override
    public Health health() {
        try {
            Integer one = jdbc.queryForObject("SELECT 1", Integer.class);
            return Health.up()
                    .withDetail("select1", one)
                    .build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
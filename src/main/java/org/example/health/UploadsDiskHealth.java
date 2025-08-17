package org.example.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.File;

@Component("uploadsDisk")
public class UploadsDiskHealth implements HealthIndicator {

    private final File path;
    private final long minFreeMb;

    public UploadsDiskHealth(
            @Value("${health.uploads.path:./uploads}") String path,
            @Value("${health.uploads.min-free-mb:100}") long minFreeMb) {
        this.path = new File(path);
        this.minFreeMb = minFreeMb;
    }

    @Override
    public Health health() {
        if (!path.exists()) {
            return Health.down().withDetail("reason", "path-missing")
                    .withDetail("path", path.getAbsolutePath())
                    .build();
        }
        long freeMb = path.getUsableSpace() / (1024 * 1024);
        return freeMb >= minFreeMb
                ? Health.up().withDetail("path", path.getAbsolutePath()).withDetail("freeMb", freeMb).build()
                : Health.outOfService().withDetail("path", path.getAbsolutePath())
                .withDetail("freeMb", freeMb).withDetail("minFreeMb", minFreeMb).build();
    }
}

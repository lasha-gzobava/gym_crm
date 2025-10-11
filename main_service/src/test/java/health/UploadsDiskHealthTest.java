package health;


import org.example.health.UploadsDiskHealth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;


public class UploadsDiskHealthTest {

    private Path tempDir;

    @BeforeEach
    void setup() throws IOException {
        tempDir = Files.createTempDirectory("uploads-disk-health-test");
    }

    @AfterEach
    void cleanup() throws IOException {
        if (tempDir != null) {
            Files.walk(tempDir)
                    .map(Path::toFile)
                    .sorted((a,b) -> -a.compareTo(b))
                    .forEach(File::delete);
        }
    }

    @Test
    void health_isDown_whenPathMissing() {
        File missing = tempDir.resolve("missing").toFile();
        UploadsDiskHealth health = new UploadsDiskHealth(missing.getAbsolutePath(), 1);

        Health result = health.health();

        assertEquals("DOWN", result.getStatus().getCode());
        assertEquals("path-missing", result.getDetails().get("reason"));
        assertEquals(missing.getAbsolutePath(), result.getDetails().get("path"));
    }

    @Test
    void health_isUp_whenEnoughFreeSpace() {
        UploadsDiskHealth health = new UploadsDiskHealth(tempDir.toString(), 1);

        Health result = health.health();

        assertEquals("UP", result.getStatus().getCode());
        assertEquals(new File(tempDir.toString()).getAbsolutePath(), result.getDetails().get("path"));
        assertTrue(((Number)result.getDetails().get("freeMb")).longValue() >= 1);
    }

    @Test
    void health_isOutOfService_whenBelowThreshold() {
        UploadsDiskHealth health = new UploadsDiskHealth(tempDir.toString(), Long.MAX_VALUE / (1024 * 1024));

        Health result = health.health();

        assertEquals("OUT_OF_SERVICE", result.getStatus().getCode());
        assertEquals(new File(tempDir.toString()).getAbsolutePath(), result.getDetails().get("path"));
        assertTrue(result.getDetails().containsKey("freeMb"));
        assertTrue(result.getDetails().containsKey("minFreeMb"));
    }
}

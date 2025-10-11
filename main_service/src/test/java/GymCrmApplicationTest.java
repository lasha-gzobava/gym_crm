

import org.example.GymCrmApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Starts the app in non-web mode and shuts it down immediately.
 * This exercises the main class and any early initialization code.
 */
class GymCrmApplicationTest {

    @Test
    void mainStartsAndStops() {
        assertDoesNotThrow(() -> {
            ConfigurableApplicationContext ctx =
                    SpringApplication.run(GymCrmApplication.class,
                            "--spring.main.web-application-type=none",
                            "--spring.main.banner-mode=off",
                            "--spring.main.lazy-initialization=false");
            ctx.close();
        });
    }
}

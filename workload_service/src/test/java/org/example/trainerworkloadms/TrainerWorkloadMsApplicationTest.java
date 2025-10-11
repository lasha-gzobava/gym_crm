package org.example.trainerworkloadms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TrainerWorkloadMsApplicationTest {

    @Test
    void contextLoads() {
        // just ensures SpringApplication.run() executes without exception
        assertDoesNotThrow(() ->
                TrainerWorkloadMsApplication.main(new String[] {})
        );
    }

    @Test
    void verifySpringApplicationStarts() {
        // check that run() returns a valid context
        assertDoesNotThrow(() -> {
            var ctx = SpringApplication.run(TrainerWorkloadMsApplication.class);
            ctx.close();
        });
    }
}

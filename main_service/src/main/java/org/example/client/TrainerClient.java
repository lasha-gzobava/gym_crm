package org.example.client;

import org.example.dto.trainer.TrainingEventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "trainer-workload-ms",          // must match spring.application.name of the target service
        path = "/workloads",
        fallback = TrainerClientFallback.class // class below
)
public interface TrainerClient {
    @GetMapping("/summaries/{username}")
    TrainingEventResponse getSummary(@PathVariable String username);
}

package org.example.client;

import org.example.dto.trainer.TrainingEventResponse;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TrainerClientFallback implements TrainerClient {

    @Override
    public TrainingEventResponse getSummary(String username) {
        TrainingEventResponse fallback = new TrainingEventResponse();
        fallback.setUsername(username);
        fallback.setIsActive(false);
        fallback.setYears(List.of());
        return fallback;
    }
}

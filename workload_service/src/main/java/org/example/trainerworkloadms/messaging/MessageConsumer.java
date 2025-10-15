package org.example.trainerworkloadms.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trainerworkloadms.dto.TrainingEventRequest;
import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.service.WorkloadService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final WorkloadService workloadService;

    @JmsListener(destination = "training.queue")
    public void receiveTrainingEvent(TrainingEventRequest event) {
        log.info(" Received TrainingEventRequest: {}", event);

        try {
            TrainingEventResponse response = workloadService.apply(event);
            log.info(" Workload successfully updated for trainer: {}", event.getUsername());
            log.debug(" Updated workload response: {}", response);
        } catch (Exception e) {
            log.error(" Failed to process training event for trainer {}: {}", event.getUsername(), e.getMessage(), e);
        }
    }
}

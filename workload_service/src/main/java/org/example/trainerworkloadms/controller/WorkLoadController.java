package org.example.trainerworkloadms.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trainerworkloadms.dto.TrainingEventRequest;
import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.service.WorkloadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workloads")
@RequiredArgsConstructor
@Slf4j
public class WorkLoadController {

    private final WorkloadService workloadService;


    @PostMapping
    public ResponseEntity<TrainingEventResponse> accept(@RequestBody TrainingEventRequest request) {
        log.info("Received training event request: {}", request);
        TrainingEventResponse response = workloadService.apply(request);
        log.info("Training event processed successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summaries/{username}")
    public ResponseEntity<TrainingEventResponse> getSummary(@PathVariable String username) {
        log.info("Fetching training summary for username: {}", username);
        return workloadService.getTrainer(username)
                .map(response -> {
                    log.info("Training summary found for username: {}", username);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("Training summary not found for username: {}", username);
                    return ResponseEntity.notFound().build();
                });
    }
}
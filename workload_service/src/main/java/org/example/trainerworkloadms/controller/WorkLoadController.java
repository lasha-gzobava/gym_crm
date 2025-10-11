package org.example.trainerworkloadms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Trainer Workload Management",
        description = "APIs for tracking trainer workloads, processing training events, and retrieving workload summaries."
)
public class WorkLoadController {

    private final WorkloadService workloadService;


    @Operation(
            summary = "Register or update a training workload event",
            description = """
            Accepts a training event (such as a completed session) and updates the trainer's workload record.
            Typically called by the main service when a new training session is completed.
            """
    )
    @PostMapping
    public ResponseEntity<TrainingEventResponse> accept(@RequestBody TrainingEventRequest request) {
        log.info("Received training event request: {}", request);
        TrainingEventResponse response = workloadService.apply(request);
        log.info("Training event processed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Fetch workload summary for a specific trainer",
            description = "Retrieves accumulated workload data for the given trainer username."
    )
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
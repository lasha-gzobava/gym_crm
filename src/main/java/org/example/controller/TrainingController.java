package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.training.TrainingAddDto;
import org.example.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/training")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Training Management", description = "Endpoints for managing trainings")
public class TrainingController {

    private final TrainingService trainingService;

    @PostMapping("/add")
    @Operation(summary = "Add a new training (authentication required)")
    public ResponseEntity<String> addTraining(
            @Valid @RequestBody TrainingAddDto dto,
            @RequestParam String password
    ) {
        log.info("Adding training for trainee: {} by trainer: {}", dto.getTraineeUsername(), dto.getTrainerUsername());
        trainingService.addTraining(dto, password);
        return ResponseEntity.ok("Training successfully added");
    }
}

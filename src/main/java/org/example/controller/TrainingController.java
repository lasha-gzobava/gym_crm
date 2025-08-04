package org.example.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.training.TrainingAddDto;
import org.example.service.TrainingService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> addTraining(
            @RequestBody TrainingAddDto dto,
            @RequestParam String password
    ) {
        try {
            trainingService.addTraining(dto, password);
            return ResponseEntity.ok("Training successfully added");
        } catch (RuntimeException e) {
            log.error("Failed to add training: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}

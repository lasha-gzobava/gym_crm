package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.*;
import org.example.dto.training.TrainerTrainingRequestDto;
import org.example.dto.training.TrainerTrainingResponseDto;
import org.example.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Trainer Management", description = "Endpoints for managing trainer accounts, profiles, and trainings")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/register")
    @Operation(summary = "Register a new trainer and return generated credentials")
    public ResponseEntity<TraineeCredentialsDto> register(@Valid @RequestBody TrainerCreateDto dto) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Attempting to register trainer: {} {}", tx, dto.getFirstName(), dto.getLastName());
        TraineeCredentialsDto credentials = trainerService.registerWithCredentials(dto);
        log.info("[{}] Trainer registered successfully with username: {}", tx, credentials.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get trainer profile (authentication required)")
    public ResponseEntity<TrainerProfileDto> getTrainer(
            @RequestParam String username,
            @RequestParam String password
    ) {
        log.info("Fetching profile for trainer: {}", username);
        TrainerProfileDto profile = trainerService.getTrainerProfile(username, password);
        log.info("Successfully fetched profile for trainer: {}", username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainer profile (authentication required)")
    public ResponseEntity<TrainerProfileDto> updateTrainer(
            @Valid @RequestBody TrainerUpdateDto dto,
            @RequestParam String password
    ) {
        log.info("Updating profile for trainer: {}", dto.getUsername());
        TrainerProfileDto profile = trainerService.updateTrainerProfile(dto, password);
        log.info("Profile updated successfully for trainer: {}", dto.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/unassigned")
    @Operation(summary = "Get trainers not assigned to the given trainee (trainee authentication required)")
    public ResponseEntity<List<TrainerForTrainerListDto>> getUnassignedTrainers(
            @RequestParam String username,
            @RequestParam String password
    ) {
        log.info("Fetching unassigned trainers for trainee: {}", username);
        List<TrainerForTrainerListDto> unassigned = trainerService.getUnassignedTrainersForTrainee(username, password);
        log.info("Found {} unassigned trainers for trainee: {}", unassigned.size(), username);
        return ResponseEntity.ok(unassigned);
    }

    @GetMapping("/trainings")
    @Operation(summary = "Get trainer's scheduled trainings (authentication required, filters optional)")
    public ResponseEntity<List<TrainerTrainingResponseDto>> getTrainerTrainings(
            @RequestParam String username,
            @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodTo,
            @RequestParam(required = false) String traineeName,
            @RequestParam String password
    ) {
        log.info("Fetching trainings for trainer: {}", username);
        TrainerTrainingRequestDto dto = new TrainerTrainingRequestDto(
                username,
                periodFrom != null ? LocalDate.parse(periodFrom) : null,
                periodTo != null ? LocalDate.parse(periodTo) : null,
                traineeName
        );
        List<TrainerTrainingResponseDto> trainings = trainerService.getTrainerTrainingsList(dto, password);
        log.info("Returned {} trainings for trainer: {}", trainings.size(), username);
        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/activate")
    @Operation(summary = "Toggle trainer's active status (authentication required)")
    public ResponseEntity<String> toggleTrainerActive(
            @Valid @RequestBody TrainerActivationDto dto,
            @RequestParam String password
    ) {
        log.info("Request to change active status for trainer: {} -> {}", dto.getUsername(), dto.getIsActive());
        trainerService.toggleActive(dto.getUsername(), dto.getIsActive(), password);
        log.info("Trainer {} is now {}", dto.getUsername(), dto.getIsActive() ? "active" : "inactive");
        return ResponseEntity.ok("Trainer " + dto.getUsername() + " is now " + (dto.getIsActive() ? "active" : "inactive"));
    }
}

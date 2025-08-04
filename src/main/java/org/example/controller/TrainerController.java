package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.*;
import org.example.dto.training.TrainerTrainingRequestDto;
import org.example.dto.training.TrainerTrainingResponseDto;
import org.example.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trainer Management", description = "Endpoints for managing trainer accounts, profiles, and trainings")
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping("/register")
    @Operation(summary = "Register a new trainer and return generated credentials")
    public ResponseEntity<TraineeCredentialsDto> register(@RequestBody TrainerCreateDto dto) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Attempting to register trainer: {} {}", tx, dto.getFirstName(), dto.getLastName());
        try {
            TraineeCredentialsDto credentials = trainerService.registerWithCredentials(dto);
            log.info("[{}] Trainer registered successfully with username: {}", tx, credentials.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
        } catch (RuntimeException e) {
            log.error("[{}] Trainer registration failed for {}: {}", tx, dto.getFirstName(), e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[{}] Unexpected error during trainer registration", tx, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get trainer profile (authentication required)")
    public ResponseEntity<TrainerProfileDto> getTrainer(
            @RequestParam String username,
            @RequestParam String password
    ) {
        log.info("Fetching profile for trainer: {}", username);
        try {
            TrainerProfileDto profile = trainerService.getTrainerProfile(username, password);
            log.info("Successfully fetched profile for trainer: {}", username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.warn("Trainer profile fetch failed for {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainer profile (authentication required)")
    public ResponseEntity<TrainerProfileDto> updateTrainer(
            @RequestBody TrainerUpdateDto dto,
            @RequestParam String password
    ) {
        log.info("Updating profile for trainer: {}", dto.getUsername());
        try {
            TrainerProfileDto profile = trainerService.updateTrainerProfile(dto, password);
            log.info("Profile updated successfully for trainer: {}", dto.getUsername());
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.warn("Trainer update failed for {}: {}", dto.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/unassigned")
    @Operation(summary = "Get trainers not assigned to the given trainee (trainee authentication required)")
    public ResponseEntity<List<TrainerForTrainerListDto>> getUnassignedTrainers(
            @RequestParam String username,
            @RequestParam String password
    ) {
        log.info("Fetching unassigned trainers for trainee: {}", username);
        try {
            List<TrainerForTrainerListDto> unassigned = trainerService.getUnassignedTrainersForTrainee(username, password);
            log.info("Found {} unassigned trainers for trainee: {}", unassigned.size(), username);
            return ResponseEntity.ok(unassigned);
        } catch (RuntimeException e) {
            log.warn("Failed to fetch unassigned trainers for {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
        try {
            TrainerTrainingRequestDto dto = new TrainerTrainingRequestDto(
                    username,
                    periodFrom != null ? LocalDate.parse(periodFrom) : null,
                    periodTo != null ? LocalDate.parse(periodTo) : null,
                    traineeName
            );
            List<TrainerTrainingResponseDto> trainings = trainerService.getTrainerTrainingsList(dto, password);
            log.info("Returned {} trainings for trainer: {}", trainings.size(), username);
            return ResponseEntity.ok(trainings);
        } catch (RuntimeException e) {
            log.warn("Failed to fetch trainings for {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/activate")
    @Operation(summary = "Toggle trainer's active status (authentication required)")
    public ResponseEntity<String> toggleTrainerActive(
            @RequestBody TrainerActivationDto dto,
            @RequestParam String password
    ) {
        log.info("Request to change active status for trainer: {} -> {}", dto.getUsername(), dto.getIsActive());
        try {
            trainerService.toggleActive(dto.getUsername(), dto.getIsActive(), password);
            log.info("Trainer {} is now {}", dto.getUsername(), dto.getIsActive() ? "active" : "inactive");
            return ResponseEntity.ok("Trainer " + dto.getUsername() + " is now " + (dto.getIsActive() ? "active" : "inactive"));
        } catch (RuntimeException e) {
            log.warn("Failed to update active status for trainer {}: {}", dto.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found or update failed.");
        }
    }
}

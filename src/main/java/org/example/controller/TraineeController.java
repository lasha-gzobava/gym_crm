package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.*;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.training.TraineeTrainingRequestDto;
import org.example.dto.training.TraineeTrainingResponseDto;
import org.example.service.TraineeService;
import org.example.service.TrainingService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trainee")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trainee Management", description = "Endpoints for managing trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainingService trainingService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new trainee and receive credentials")
    public ResponseEntity<TraineeCredentialsDto> register(
            @Valid @RequestBody TraineeCreateDto dto) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Registering trainee: {} {}", tx, dto.getUser().getFirstName(), dto.getUser().getLastName());
        TraineeCredentialsDto credentials = traineeService.registerWithCredentials(dto);
        log.info("[{}] Registration successful for: {}", tx, credentials.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get trainee profile (requires password)")
    public ResponseEntity<TraineeProfileDto> getProfile(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching profile for trainee: {}", tx, username);
        try {
            userService.authenticate(username, password);
            TraineeProfileDto profile = traineeService.getTraineeProfile(username, password);
            log.info("[{}] Profile fetch successful for: {}", tx, username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.warn("[{}] Failed to fetch profile for {}: {}", tx, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainee profile (requires password)")
    public ResponseEntity<TraineeProfileDto> updateProfile(
            @Valid @RequestBody TraineeProfileUpdateDto dto,
            @RequestParam String password
    ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Updating profile for trainee: {}", tx, dto.getUsername());
        try {
            TraineeProfileDto update = traineeService.updateProfile(dto, password);
            log.info("[{}] Profile update successful for: {}", tx, dto.getUsername());
            return ResponseEntity.ok(update);
        } catch (RuntimeException e) {
            log.warn("[{}] Profile update failed for {}: {}", tx, dto.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete trainee account (requires password)")
    public ResponseEntity<String> deleteProfile(
            @Valid @RequestBody TraineeDeleteRequestDto dto
    ) {
        String tx = UUID.randomUUID().toString();
        log.warn("[{}] Attempting to delete trainee: {}", tx, dto.getUsername());
        try {
            traineeService.deleteByUsername(dto.getUsername(), dto.getPassword());
            log.warn("[{}] Successfully deleted trainee: {}", tx, dto.getUsername());
            return ResponseEntity.ok("Trainee " + dto.getUsername() + " deleted successfully");
        } catch (RuntimeException e) {
            log.warn("[{}] Deletion failed for trainee {}: {}", tx, dto.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found or auth failed.");
        } catch (Exception e) {
            log.error("[{}] Unexpected error during trainee deletion: {}", tx, dto.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting trainee.");
        }
    }

    @PutMapping("/trainers")
    @Operation(summary = "Update list of trainers assigned to trainee (requires password)")
    public ResponseEntity<List<TrainerForTrainerListDto>> updateTraineeTrainers(
            @Valid @RequestBody TraineeTrainerUpdateDto dto,
            @RequestParam String password
    ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Updating trainers for trainee: {}", tx, dto.getTraineeUsername());
        try {
            List<TrainerForTrainerListDto> updatedList = traineeService.updateTraineeTrainers(dto, password);
            log.info("[{}] Trainer list updated for trainee: {}", tx, dto.getTraineeUsername());
            return ResponseEntity.ok(updatedList);
        } catch (RuntimeException e) {
            log.warn("[{}] Failed to update trainer list for {}: {}", tx, dto.getTraineeUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/trainings")
    @Operation(summary = "Get list of trainings for trainee (requires password)")
    public ResponseEntity<List<TraineeTrainingResponseDto>> getTraineeTrainings(
            @RequestParam String username,
            @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) String trainingType,
            @RequestParam String password
    ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Fetching trainings for trainee: {}", tx, username);
        try {
            LocalDate from = periodFrom != null && !periodFrom.isBlank() ? LocalDate.parse(periodFrom) : null;
            LocalDate to = periodTo != null && !periodTo.isBlank() ? LocalDate.parse(periodTo) : null;

            TraineeTrainingRequestDto dto = new TraineeTrainingRequestDto(
                    username, from, to, trainerName, trainingType
            );

            List<TraineeTrainingResponseDto> trainings = traineeService.getTraineeTrainingsList(dto, password);
            log.info("[{}] Successfully fetched trainings for: {}", tx, username);
            return ResponseEntity.ok(trainings);

        } catch (DateTimeParseException e) {
            log.warn("[{}] Invalid date format in request for {}: {}", tx, username, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            log.warn("[{}] Failed to get trainings for {}: {}", tx, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/activate")
    @Operation(summary = "Activate or deactivate trainee (requires password)")
    public ResponseEntity<String> toggleTraineeActive(
            @Valid @RequestBody TraineeActivationDto dto,
            @RequestParam String password
    ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Attempting to set active={} for trainee: {}", tx, dto.getIsActive(), dto.getUsername());
        try {
            traineeService.toggleActive(dto.getUsername(), dto.getIsActive(), password);
            log.info("[{}] Trainee {} is now {}", tx, dto.getUsername(), dto.getIsActive() ? "active" : "inactive");
            return ResponseEntity.ok("Trainee " + dto.getUsername() + " is now " + (dto.getIsActive() ? "active" : "inactive"));
        } catch (RuntimeException e) {
            log.warn("[{}] Failed to change activation for {}: {}", tx, dto.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found or error occurred.");
        }
    }
}
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
import java.util.List;

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
    public ResponseEntity<TraineeCredentialsDto> register(@Valid @RequestBody TraineeCreateDto dto) {
        log.info("Registering trainee: {} {}", dto.getUser().getFirstName(), dto.getUser().getLastName());
        TraineeCredentialsDto credentials = traineeService.registerWithCredentials(dto);
        log.info("Registration successful for: {}", credentials.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(credentials);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get trainee profile (requires password)")
    public ResponseEntity<TraineeProfileDto> getProfile(
            @RequestParam String username,
            @RequestParam String password
    ) {
        log.info("Fetching profile for trainee: {}", username);
        userService.authenticate(username, password);
        TraineeProfileDto profile = traineeService.getTraineeProfile(username, password);
        log.info("Profile fetch successful for: {}", username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update trainee profile (requires password)")
    public ResponseEntity<TraineeProfileDto> updateProfile(
            @Valid @RequestBody TraineeProfileUpdateDto dto,
            @RequestParam String password
    ) {
        log.info("Updating profile for trainee: {}", dto.getUsername());
        TraineeProfileDto updated = traineeService.updateProfile(dto, password);
        log.info("Profile update successful for: {}", dto.getUsername());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    @Operation(summary = "Delete trainee account (requires password)")
    public ResponseEntity<String> deleteProfile(@Valid @RequestBody TraineeDeleteRequestDto dto) {
        log.warn("Attempting to delete trainee: {}", dto.getUsername());
        traineeService.deleteByUsername(dto.getUsername(), dto.getPassword());
        log.warn("Successfully deleted trainee: {}", dto.getUsername());
        return ResponseEntity.ok("Trainee " + dto.getUsername() + " deleted successfully");
    }

    @PutMapping("/trainers")
    @Operation(summary = "Update list of trainers assigned to trainee (requires password)")
    public ResponseEntity<List<TrainerForTrainerListDto>> updateTraineeTrainers(
            @Valid @RequestBody TraineeTrainerUpdateDto dto,
            @RequestParam String password
    ) {
        log.info("Updating trainers for trainee: {}", dto.getTraineeUsername());
        List<TrainerForTrainerListDto> updatedList = traineeService.updateTraineeTrainers(dto, password);
        log.info("Trainer list updated for trainee: {}", dto.getTraineeUsername());
        return ResponseEntity.ok(updatedList);
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
        log.info("Fetching trainings for trainee: {}", username);

        LocalDate from = (periodFrom == null || periodFrom.isBlank()) ? null : LocalDate.parse(periodFrom); // DateTimeParseException handled globally
        LocalDate to   = (periodTo   == null || periodTo.isBlank())   ? null : LocalDate.parse(periodTo);

        TraineeTrainingRequestDto dto = new TraineeTrainingRequestDto(
                username, from, to, trainerName, trainingType
        );

        List<TraineeTrainingResponseDto> trainings = traineeService.getTraineeTrainingsList(dto, password);
        log.info("Successfully fetched trainings for: {}", username);
        return ResponseEntity.ok(trainings);
    }

    @PatchMapping("/activate")
    @Operation(summary = "Activate or deactivate trainee (requires password)")
    public ResponseEntity<String> toggleTraineeActive(
            @Valid @RequestBody TraineeActivationDto dto,
            @RequestParam String password
    ) {
        log.info("Attempting to set active={} for trainee: {}", dto.getIsActive(), dto.getUsername());
        traineeService.toggleActive(dto.getUsername(), dto.getIsActive(), password);
        log.info("Trainee {} is now {}", dto.getUsername(), dto.getIsActive() ? "active" : "inactive");
        return ResponseEntity.ok("Trainee " + dto.getUsername() + " is now " + (dto.getIsActive() ? "active" : "inactive"));
    }
}

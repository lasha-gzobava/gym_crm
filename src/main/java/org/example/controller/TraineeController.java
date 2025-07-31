package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.CreateTraineeDto;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainee.TraineeProfileDto;
import org.example.dto.trainee.TraineeProfileUpdateDto;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trainee")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trainee Management", description = "Endpoints for managing trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final UserService userService;
    private final TrainingService trainingService;

    @PostMapping("/register")
    @Operation(summary = "Register trainee and return generated credentials")
    public ResponseEntity<TraineeCredentialsDto> register(
            @RequestBody CreateTraineeDto dto
    ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Registering trainee: {} {}", tx, dto.getUser().getFirstName(), dto.getUser().getLastName());
        TraineeCredentialsDto credentials = traineeService.registerWithCredentials(dto);
        return ResponseEntity.status(201).body(credentials);
    }

    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileDto> getProfile(@RequestParam String username) {
        log.info("Received request to fetch profile for trainee with username: {}", username);
        try {
            TraineeProfileDto profile = traineeService.getTraineeProfile(username);
            log.info("Successfully fetched profile for username: {}", username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            log.error("Failed to fetch profile for username: {} - {}", username, e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<TraineeProfileDto> updateProfile(@RequestBody TraineeProfileUpdateDto dto){
        log.info("Received request to update profile for username: {}", dto.getUsername());
        try {
            TraineeProfileDto update = traineeService.updateProfile(dto);
            log.info("Successfully updated profile for username: {}", dto.getUsername());
            return ResponseEntity.ok(update);
        }catch (RuntimeException e){
            log.error("Error updating profile for {}: {}", dto.getUsername(), e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }
}

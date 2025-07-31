package org.example.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.CreateTrainerDto;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/trainer")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Trainer Management", description = "Endpoints for managing trainers")
public class TrainerController {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final UserService userService;
    private final TrainingService trainingService;

    @PostMapping("/register")
    @Operation(summary = "Register trainer and return generated credentials")
    public ResponseEntity<TraineeCredentialsDto> register(
            @RequestBody CreateTrainerDto dto
            ) {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Registering trainer: {} {}", tx, dto.getUser().getFirstName(), dto.getUser().getLastName());
        TraineeCredentialsDto credentials = trainerService.registerWithCredentials(dto);
        return ResponseEntity.status(201).body(credentials);
    }
}

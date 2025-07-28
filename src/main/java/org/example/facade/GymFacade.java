package org.example.facade;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.service.*;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final Validator validator;

    // --- Trainee ---
    public TraineeDto createTrainee(CreateTraineeDto dto) {
        validate(dto);
        return traineeService.createTrainee(dto);
    }

    public TraineeDto getTrainee(String username) {
        return traineeService.getByUsername(username);
    }

    public void updateTrainee(String username, CreateTraineeDto dto) {
        validate(dto);
        traineeService.updateTrainee(username, dto);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteByUsername(username);
    }

    public void toggleTraineeStatus(String username) {
        traineeService.toggleActive(username);
    }

    public void changeTraineePassword(PasswordChangeDto dto) {
        validate(dto);
        traineeService.changePassword(dto);
    }

    // --- Trainer ---
    public TrainerDto createTrainer(CreateTrainerDto dto) {
        validate(dto);
        return trainerService.createTrainer(dto);
    }

    public TrainerDto getTrainer(String username) {
        return trainerService.getByUsername(username);
    }

    public void updateTrainer(String username, CreateTrainerDto dto) {
        validate(dto);
        trainerService.updateTrainer(username, dto);
    }

    public void toggleTrainerStatus(String username) {
        trainerService.toggleActive(username);
    }

    public void changeTrainerPassword(PasswordChangeDto dto) {
        validate(dto);
        trainerService.changePassword(dto);
    }

    // --- Training ---
    public TrainingDto addTraining(CreateTrainingDto dto) {
        validate(dto);
        return trainingService.addTraining(dto);
    }

    public List<TrainingDto> getTraineeTrainings(String username) {
        return trainingService.getTrainingsForTrainee(username);
    }

    public List<TrainingDto> getTrainerTrainings(String username) {
        return trainingService.getTrainingsForTrainer(username);
    }

    // --- Shared ---
    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            log.warn("Validation failed for {}: {}", dto.getClass().getSimpleName(), violations);
            throw new ConstraintViolationException(violations);
        }
    }
}

package org.example.facade;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.service.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
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

    public TraineeDto getTrainee(String username, String password) {
        userService.authenticate(username, password);
        return traineeService.getByUsername(username, password);
    }

    public void updateTrainee(String username, String password, CreateTraineeDto dto) {
        validate(dto);
        traineeService.updateTrainee(username, dto, password);
    }

    public void deleteTrainee(String username, String password) {
        userService.authenticate(username, password);
        traineeService.deleteByUsername(username, password);
    }

    public void toggleTraineeStatus(String username, String password) {
        userService.authenticate(username, password);
        traineeService.toggleActive(username, password);
    }

    public void changeTraineePassword(PasswordChangeDto dto) {
        validate(dto);
        userService.authenticate(dto.getUsername(), dto.getOldPassword());
        traineeService.changePassword(dto);
    }

    // --- Trainer ---

    public TrainerDto createTrainer(CreateTrainerDto dto) {
        validate(dto);
        return trainerService.createTrainer(dto);
    }

    public TrainerDto getTrainer(String username, String password) {
        userService.authenticate(username, password);
        return trainerService.getByUsername(username, password);
    }

    public void updateTrainer(String username, String password, CreateTrainerDto dto) {
        validate(dto);
        userService.authenticate(username, password);
        trainerService.updateTrainer(username, dto, password);
    }

    public void toggleTrainerStatus(String username, String password) {
        userService.authenticate(username, password);
        trainerService.toggleActive(username, password);
    }

    public void changeTrainerPassword(PasswordChangeDto dto) {
        validate(dto);
        userService.authenticate(dto.getUsername(), dto.getOldPassword());
        trainerService.changePassword(dto);
    }

    // --- Training ---

    public TrainingDto addTraining(CreateTrainingDto dto, String trainerUsername, String trainerPassword) {
        validate(dto);
        userService.authenticate(trainerUsername, trainerPassword);
        return trainingService.addTraining(dto);
    }

    public List<TrainingDto> getTraineeTrainings(String username, String password) {
        userService.authenticate(username, password);
        return trainingService.getTrainingsForTrainee(username, password);
    }

    public List<TrainingDto> getTrainerTrainings(String username, String password) {
        userService.authenticate(username, password);
        return trainingService.getTrainingsForTrainer(username, password);
    }

    // --- Extended: Filtered Training Access ---

    public List<TrainingDto> getTraineeTrainingsFiltered(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingType
    ) {
        userService.authenticate(username, password);
        return trainingService.getTrainingsForTrainee(username, password, fromDate, toDate, trainerName, trainingType);
    }


    public List<TrainingDto> getTrainerTrainingsFiltered(
            String username,
            String password,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
        userService.authenticate(username, password);
        return trainingService.getTrainingsForTrainer(username, password, fromDate, toDate, traineeName);
    }


    // --- Trainer Assignment Management ---

    public List<TrainerDto> getUnassignedTrainers(String traineeUsername, String password) {
        userService.authenticate(traineeUsername, password);
        return trainerService.getUnassignedTrainersForTrainee(traineeUsername);
    }

    public void updateTraineeTrainers(String traineeUsername, String password, List<Long> trainerIds) {
        userService.authenticate(traineeUsername, password);
        traineeService.updateTrainersList(traineeUsername, trainerIds);
    }

    // --- Shared Validation ---
    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            log.warn("Validation failed for {}: {}", dto.getClass().getSimpleName(), violations);
            throw new ConstraintViolationException(violations);
        }
    }
}

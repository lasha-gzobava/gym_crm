package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainee.TraineeInfoDto;
import org.example.dto.trainer.TrainerCreateDto;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.trainer.TrainerProfileDto;
import org.example.dto.trainer.TrainerUpdateDto;
import org.example.dto.training.TrainerTrainingRequestDto;
import org.example.dto.training.TrainerTrainingResponseDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.repository.*;
import org.example.service.TrainerService;
import org.example.service.UserService;
import org.example.util.UsernamePasswordGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TrainingRepository trainingRepository;

    @Override
    @Transactional
    public TraineeCredentialsDto registerWithCredentials(TrainerCreateDto dto) {
        log.info("Registering trainer: {} {}", dto.getFirstName(), dto.getLastName());

        // Check if already a trainee
        String attemptedUsername = UsernamePasswordGenerator.generateStaticUsername(
                dto.getFirstName(), dto.getLastName());

        if (traineeRepository.existsByUser_Username(attemptedUsername)) {
            log.warn("User already registered as a trainee.");
            throw new IllegalStateException("User already registered as a trainee.");
        }

        User user = userService.createUser(dto.getFirstName(), dto.getLastName());
        user.getRoles().add("ROLE_TRAINER");
        userRepository.save(user);

        TrainingType specialization = trainingTypeRepository.findByTrainingTypeName(dto.getSpecialization())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        Trainer trainer = new Trainer(specialization, user);
        trainerRepository.save(trainer);

        log.info("Trainer registered with username: {}", user.getUsername());
        return new TraineeCredentialsDto(user.getUsername(), userService.getRawPassword());
    }


    @Override
    public TrainerProfileDto getTrainerProfile(String username, String password) {
        log.debug("Fetching profile for trainer: {}", username);
        userService.authenticate(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        List<Trainee> traineeList = traineeRepository.findAllByTrainers_User_Username(username);

        List<TraineeInfoDto> trainees = traineeList.stream()
                .map(trainee -> new TraineeInfoDto(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                )).toList();

        log.info("Trainer profile retrieved for username: {}", username);
        return new TrainerProfileDto(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                trainer.getUser().getIsActive(),
                trainees
        );
    }

    @Override
    @Transactional
    public TrainerProfileDto updateTrainerProfile(TrainerUpdateDto dto, String password) {
        log.info("Updating profile for trainer: {}", dto.getUsername());
        userService.authenticate(dto.getUsername(), password);

        Trainer trainer = trainerRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainer not found during update: {}", dto.getUsername());
                    return new RuntimeException("Trainer not found");
                });

        User user = trainer.getUser();

        //Username should be unchanged
        if (!dto.getUsername().equals(user.getUsername())) {
            log.error("Attempted to change username from {} to {}", user.getUsername(), dto.getUsername());
            throw new IllegalArgumentException("Username cannot be changed.");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        TrainingType specialization = trainingTypeRepository.findByTrainingTypeName(dto.getSpecialization())
                .orElseThrow(() -> {
                    log.error("Specialization not found during profile update: {}", dto.getSpecialization());
                    return new RuntimeException("Specialization not found");
                });

        trainer.setSpecialization(specialization);
        user.setIsActive(dto.getIsActive());

        List<Trainee> traineeList = traineeRepository.findAllByTrainers_User_Username(dto.getUsername());
        List<TraineeInfoDto> trainees = traineeList.stream()
                .map(trainee -> new TraineeInfoDto(
                        trainee.getUser().getUsername(),
                        trainee.getUser().getFirstName(),
                        trainee.getUser().getLastName()
                )).toList();

        log.info("Trainer profile updated for: {}", dto.getUsername());

        return new TrainerProfileDto(
                trainer.getUser().getUsername(),
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getSpecialization().getTrainingTypeName(),
                trainer.getUser().getIsActive(),
                trainees
        );
    }

    @Override
    @Transactional
    public boolean toggleActive(String username, boolean isActive, String password) {
        log.info("Toggling trainer '{}' active status to: {}", username, isActive);
        userService.authenticate(username, password);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for trainer toggle: {}", username);
                    return new RuntimeException("User not found");
                });

        if (Boolean.TRUE.equals(user.getIsActive()) == isActive) {
            log.info("Trainer {} is already {}", username, isActive ? "active" : "inactive");
            return false;
        }

        user.setIsActive(isActive);
        log.info("Trainer '{}' active status set to: {}", username, isActive);
        return true;
    }



    @Override
    public List<TrainerForTrainerListDto> getUnassignedTrainersForTrainee(String traineeUsername, String password) {
        log.debug("Fetching unassigned trainers for trainee: {}", traineeUsername);
        userService.authenticate(traineeUsername, password);

        Trainee trainee = traineeRepository.findWithTrainersByUserUsername(traineeUsername)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", traineeUsername);
                    return new RuntimeException("Trainee not found");
                });

        List<Trainer> allTrainers = trainerRepository.findAll();

        List<TrainerForTrainerListDto> result = allTrainers.stream()
                .filter(trainer -> !trainee.getTrainers().contains(trainer))
                .map(trainer -> new TrainerForTrainerListDto(
                        trainer.getUser().getUsername(),
                        trainer.getUser().getFirstName(),
                        trainer.getUser().getLastName(),
                        trainer.getSpecialization().getTrainingTypeName()
                )).toList();

        log.info("Found {} unassigned trainers for trainee: {}", result.size(), traineeUsername);
        return result;
    }

    @Override
    public List<TrainerTrainingResponseDto> getTrainerTrainingsList(TrainerTrainingRequestDto dto, String password) {
        log.debug("Fetching trainings for trainer: {}", dto.getUsername());
        userService.authenticate(dto.getUsername(), password);

        Trainer trainer = trainerRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.warn("Trainer not found when fetching trainings: {}", dto.getUsername());
                    return new RuntimeException("Trainer not found");
                });

        List<TrainerTrainingResponseDto> result = trainingRepository.findAllByTrainer(trainer).stream()
                .filter(training -> {
                    if (dto.getPeriodFrom() != null && training.getTrainingDate().isBefore(dto.getPeriodFrom())) return false;
                    if (dto.getPeriodTo() != null && training.getTrainingDate().isAfter(dto.getPeriodTo())) return false;
                    return dto.getTraineeName() == null ||
                            training.getTrainee().getUser().getFullName().equalsIgnoreCase(dto.getTraineeName());
                })
                .map(training -> new TrainerTrainingResponseDto(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainee().getUser().getFullName()
                )).toList();

        log.info("Returning {} trainings for trainer: {}", result.size(), dto.getUsername());
        return result;
    }



}

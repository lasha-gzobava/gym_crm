package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.dto.trainee.*;
import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.trainer.TrainerInfoDto;
import org.example.dto.training.TraineeTrainingRequestDto;
import org.example.dto.training.TraineeTrainingResponseDto;
import org.example.entity.*;
import org.example.mapper.TraineeMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.UserRepository;
import org.example.service.TraineeService;
import org.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TraineeMapper traineeMapper;
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingRepository trainingRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TraineeCredentialsDto registerWithCredentials(TraineeCreateDto dto) {
        log.info("Registering trainee: {} {}", dto.getUser().getFirstName(), dto.getUser().getLastName());

        User user = userService.createUser(
                dto.getUser().getFirstName(),
                dto.getUser().getLastName()
        );

        Trainee trainee = new Trainee(dto.getDateOfBirth(), dto.getAddress(), user);
        traineeRepository.save(trainee);

        log.info("Trainee registered with username: {}", user.getUsername());
        return new TraineeCredentialsDto(user.getUsername(), userService.getRawPassword());
    }

    @Override
    public TraineeProfileDto getTraineeProfile(String username, String password) {
        log.debug("Fetching profile for trainee: {}", username);
        userService.authenticate(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        List<TrainerInfoDto> trainerDtos = trainee.getTrainers().stream()
                .map(x -> new TrainerInfoDto(
                        x.getUser().getUsername(),
                        x.getUser().getFirstName(),
                        x.getUser().getLastName(),
                        x.getSpecialization().getTrainingTypeName()
                )).toList();

        User user = trainee.getUser();

        log.debug("Returning profile for trainee: {}", username);
        return new TraineeProfileDto(
                user.getFirstName(),
                user.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                user.getIsActive(),
                trainerDtos
        );
    }

    @Override
    public TraineeProfileDto updateProfile(TraineeProfileUpdateDto dto, String password) {
        log.info("Updating profile for trainee: {}", dto.getUsername());
        userService.authenticate(dto.getUsername(), password);

        Trainee trainee = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found during update: {}", dto.getUsername());
                    return new RuntimeException("Trainee not found");
                });

        User user = trainee.getUser();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getDateOfBirth() != null) trainee.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getAddress() != null) trainee.setAddress(dto.getAddress());
        user.setIsActive(dto.getIsActive());

        log.info("Profile updated for trainee: {}", dto.getUsername());

        List<TrainerInfoDto> trainerDtos = trainee.getTrainers().stream()
                .map(x -> new TrainerInfoDto(
                        x.getUser().getUsername(),
                        x.getUser().getFirstName(),
                        x.getUser().getLastName(),
                        x.getSpecialization().getTrainingTypeName()
                )).toList();

        return new TraineeProfileDto(
                user.getFirstName(),
                user.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                user.getIsActive(),
                trainerDtos
        );
    }

    @Override
    @Transactional
    public void deleteByUsername(String username, String password) {
        log.warn("Attempting to delete trainee: {}", username);
        userService.authenticate(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for deletion: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        trainingRepository.deleteAllByTrainee(trainee);
        traineeRepository.delete(trainee);

        log.warn("Trainee deleted: {}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username, boolean isActive, String password) {
        log.info("Toggling active status for trainee: {} to {}", username, isActive);
        userService.authenticate(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found to toggle active status: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        trainee.getUser().setIsActive(isActive);
        log.info("Active status set to {} for trainee: {}", isActive, username);
    }

    @Override
    @Transactional
    public List<TrainerForTrainerListDto> updateTraineeTrainers(TraineeTrainerUpdateDto dto, String password) {
        log.info("Updating trainers for trainee: {}", dto.getTraineeUsername());
        userService.authenticate(dto.getTraineeUsername(), password);

        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found to update trainers: {}", dto.getTraineeUsername());
                    return new RuntimeException("Trainee not found");
                });

        List<Trainer> trainers = trainerRepository.findAllByUser_UsernameIn(dto.getTrainerUsernames());
        if (trainers.size() != dto.getTrainerUsernames().size()) {
            log.error("Mismatch in provided trainers for trainee: {}", dto.getTraineeUsername());
            throw new RuntimeException("Some trainer usernames not found");
        }

        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);

        log.info("Updated trainers for trainee: {}", dto.getTraineeUsername());

        return trainers.stream()
                .map(t -> new TrainerForTrainerListDto(
                        t.getUser().getUsername(),
                        t.getUser().getFirstName(),
                        t.getUser().getLastName(),
                        t.getSpecialization().getTrainingTypeName()
                ))
                .toList();
    }

    @Override
    public List<TraineeTrainingResponseDto> getTraineeTrainingsList(TraineeTrainingRequestDto dto, String password) {
        log.debug("Fetching trainings list for trainee: {}", dto.getUsername());
        userService.authenticate(dto.getUsername(), password);

        Trainee trainee = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> {
                    log.error("Trainee not found for training list: {}", dto.getUsername());
                    return new RuntimeException("Trainee not found");
                });

        List<TraineeTrainingResponseDto> trainings = trainingRepository.findAllByTrainee(trainee).stream()
                .filter(training -> {
                    if (dto.getPeriodFrom() != null && training.getTrainingDate().isBefore(dto.getPeriodFrom())) {
                        return false;
                    }
                    if (dto.getPeriodTo() != null && training.getTrainingDate().isAfter(dto.getPeriodTo())) {
                        return false;
                    }
                    if (dto.getTrainerName() != null &&
                            !training.getTrainer().getUser().getFirstName().equalsIgnoreCase(dto.getTrainerName())) {
                        return false;
                    }
                    if (dto.getTrainingType() != null &&
                            !training.getTrainingType().getTrainingTypeName().equalsIgnoreCase(dto.getTrainingType())) {
                        return false;
                    }
                    return true;
                })
                .map(training -> new TraineeTrainingResponseDto(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType().getTrainingTypeName(),
                        training.getTrainingDuration(),
                        training.getTrainer().getUser().getFirstName() + " " + training.getTrainer().getUser().getLastName()
                ))
                .toList();

        log.debug("Returning {} trainings for trainee: {}", trainings.size(), dto.getUsername());
        return trainings;
    }
}

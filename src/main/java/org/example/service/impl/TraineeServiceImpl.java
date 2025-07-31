package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.dto.trainee.*;
import org.example.dto.login.PasswordChangeDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.example.mapper.TraineeMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.UserRepository;
import org.example.service.TraineeService;
import org.example.service.UserService;
import org.example.util.UsernamePasswordGenerator;
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
    public TraineeDto createTrainee(CreateTraineeDto dto) {
        log.info("Creating new trainee for {} {}", dto.getUser().getFirstName(), dto.getUser().getLastName());

        User user = userService.createUser(
                dto.getUser().getFirstName(),
                dto.getUser().getLastName()
        );

        Trainee trainee = new Trainee(dto.getDateOfBirth(), dto.getAddress(), user);
        traineeRepository.save(trainee);

        log.info("Trainee created with username: {}", user.getUsername());
        return traineeMapper.toDto(trainee);
    }

    @Override
    @Transactional
    public TraineeCredentialsDto registerWithCredentials(CreateTraineeDto dto) {
        User user = userService.createUser(
                dto.getUser().getFirstName(),
                dto.getUser().getLastName()
        );

        Trainee trainee = new Trainee(dto.getDateOfBirth(), dto.getAddress(), user);
        traineeRepository.save(trainee);

        return new TraineeCredentialsDto(user.getUsername(), userService.getRawPassword());
    }



    @Override
    public TraineeDto getByUsername(String username, String password) {
        log.debug("Authenticating and fetching trainee by username: {}", username);
        userService.authenticate(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        return traineeMapper.toDto(trainee);
    }

    @Override
    public TraineeProfileDto getTraineeProfile(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<TrainerInfoDto> trainerDtos = trainee.getTrainers().stream()
                .map(x -> new TrainerInfoDto(
                        x.getUser().getUsername(),
                        x.getUser().getFirstName(),
                        x.getUser().getLastName(),
                        x.getSpecialization().getTrainingTypeName()
                )).toList();

        User user = trainee.getUser();

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
    public TraineeProfileDto updateProfile(TraineeProfileUpdateDto dto) {
        Trainee trainee = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        User user = trainee.getUser();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        if (dto.getDateOfBirth() != null) trainee.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getAddress() != null) trainee.setAddress(dto.getAddress());
        user.setIsActive(dto.getIsActive());

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
    public void changePassword(PasswordChangeDto dto) {
        userService.changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());
    }

    @Override
    @Transactional
    public void updateTrainee(String username, CreateTraineeDto dto, String password) {
        log.info("Updating trainee profile: {}", username);
        userService.authenticate(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        trainee.getUser().setFirstName(dto.getUser().getFirstName());
        trainee.getUser().setLastName(dto.getUser().getLastName());
        trainee.setAddress(dto.getAddress());
        trainee.setDateOfBirth(dto.getDateOfBirth());

        traineeRepository.save(trainee);
        log.info("Trainee updated: {}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username, String password) {
        log.info("Toggling active status for trainee: {}", username);
        userService.authenticate(username, password);
        userService.toggleActive(username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username, String password) {
        log.warn("Deleting trainee with auth check: {}", username);
        userService.authenticate(username, password);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        trainingRepository.deleteAllByTrainee(trainee);
        traineeRepository.delete(trainee);

        log.warn("Trainee deleted: {}", username);
    }


    @Override
    @Transactional
    public void updateAssignedTrainers(String username, List<Long> trainerIds) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Trainer> newTrainers = trainerRepository.findAllById(trainerIds);
        trainee.setTrainers(newTrainers);

        traineeRepository.save(trainee);
    }

    @Override
    @Transactional
    public void updateTrainersList(String traineeUsername, List<Long> trainerIds) {
        log.info("Updating trainers list for trainee: {}", traineeUsername);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Trainer> trainers = trainerRepository.findAllById(trainerIds);
        if (trainers.size() != trainerIds.size()) {
            throw new RuntimeException("One or more trainer IDs are invalid.");
        }

        trainee.setTrainers(trainers);
        traineeRepository.save(trainee);

        log.info("Trainee '{}' trainers updated: {}", traineeUsername, trainerIds);
    }


}

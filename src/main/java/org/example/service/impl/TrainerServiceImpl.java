package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainee.CreateTraineeDto;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.CreateTrainerDto;
import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainer.TrainerDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.mapper.TrainerMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainerService;
import org.example.service.UserService;
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
    private final TrainerMapper trainerMapper;
    private final UserService userService;

    @Override
    @Transactional
    public TrainerDto createTrainer(CreateTrainerDto dto) {
        log.info("Creating new trainer for {} {}", dto.getUser().getFirstName(), dto.getUser().getLastName());

        TrainingType specialization = trainingTypeRepository.findByTrainingTypeName(dto.getSpecialization())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        User user = userService.createUser(
                dto.getUser().getFirstName(),
                dto.getUser().getLastName()
        );

        Trainer trainer = new Trainer(specialization, user);
        trainerRepository.save(trainer);

        log.info("Trainer created with username: {}", user.getUsername());
        return trainerMapper.toDto(trainer);
    }

    @Override
    public TraineeCredentialsDto registerWithCredentials(CreateTrainerDto dto) {
        User user = userService.createUser(
                dto.getUser().getFirstName(),
                dto.getUser().getLastName()
        );

        TrainingType specialization = trainingTypeRepository.findByTrainingTypeName(dto.getSpecialization())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        Trainer trainer = new Trainer(
                specialization,
                user
        );

        return new TraineeCredentialsDto(user.getUsername(), userService.getRawPassword());
    }

    @Override
    public TrainerDto getByUsername(String username, String password) {
        log.debug("Authenticating and fetching trainer: {}", username);
        userService.authenticate(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        return trainerMapper.toDto(trainer);
    }

    @Override
    @Transactional
    public void changePassword(PasswordChangeDto dto) {
        userService.changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());
    }

    @Override
    @Transactional
    public void updateTrainer(String username, CreateTrainerDto dto, String password) {
        log.info("Updating trainer: {}", username);
        userService.authenticate(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        trainer.getUser().setFirstName(dto.getUser().getFirstName());
        trainer.getUser().setLastName(dto.getUser().getLastName());

        TrainingType specialization = trainingTypeRepository.findByTrainingTypeName(dto.getSpecialization())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        trainer.setSpecialization(specialization);
        trainerRepository.save(trainer);
        log.info("Trainer updated: {}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username, String password) {
        userService.authenticate(username, password);
        userService.toggleActive(username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username, String password) {
        log.warn("Deleting trainer by username: {}", username);
        userService.authenticate(username, password);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        trainerRepository.delete(trainer);
        log.warn("Trainer deleted: {}", username);
    }

    public List<TrainerDto> getUnassignedTrainersForTrainee(String traineeUsername) {
        Trainee trainee = traineeRepository.findWithTrainersByUserUsername(traineeUsername)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Trainer> allTrainers = trainerRepository.findAll();

        return allTrainers.stream()
                .filter(trainer -> !trainee.getTrainers().contains(trainer))
                .map(trainerMapper::toDto)
                .toList();
    }

}

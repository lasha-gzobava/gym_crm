package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateTrainerDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.TrainerDto;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.mapper.TrainerMapper;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainerService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
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
    public TrainerDto getByUsername(String username) {
        log.debug("Fetching trainer by username: {}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: {}", username);
                    return new RuntimeException("Trainer not found");
                });
        log.debug("Returning trainer DTO for username: {}", username);
        return trainerMapper.toDto(trainer);
    }

    @Override
    @Transactional
    public void changePassword(PasswordChangeDto dto) {
        userService.changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());
    }

    @Override
    @Transactional
    public void updateTrainer(String username, CreateTrainerDto dto) {
        log.info("Updating trainer profile: {}", username);
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
    public void toggleActive(String username) {
        userService.toggleActive(username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username) {
        log.warn("Deleting trainer by username: {}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        trainerRepository.delete(trainer);
        log.warn("Trainer deleted: {}", username);
    }
}
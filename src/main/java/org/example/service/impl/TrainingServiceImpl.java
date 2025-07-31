package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.training.CreateTrainingDto;
import org.example.dto.training.TrainingDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.mapper.TrainingMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;
    private final UserService userService;

    @Override
    @Transactional
    public TrainingDto addTraining(CreateTrainingDto dto) {
        log.info("Adding training: {}", dto.getTrainingName());


//        userService.authenticate(dto.getTrainerUsername(), dto.getTrainerPassword());

        Trainer trainer = trainerRepository.findById(dto.getTrainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        Trainee trainee = traineeRepository.findById(dto.getTraineeId())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        TrainingType trainingType = trainingTypeRepository.findById(dto.getTrainingTypeId())
                .orElseThrow(() -> new RuntimeException("Training type not found"));

        Training training = new Training(
                trainee,
                trainer,
                dto.getTrainingName(),
                trainingType,
                dto.getTrainingDate(),
                (long) dto.getTrainingDuration()
        );

        trainingRepository.save(training);
        log.info("Training added for trainee {} with trainer {}", trainee.getUser().getUsername(), trainer.getUser().getUsername());

        return trainingMapper.toDto(training);
    }

    @Override
    public List<TrainingDto> getTrainingsForTrainee(String username, String password) {
        log.info("Fetching trainings for trainee: {}", username);
        userService.authenticate(username, password);

        return trainingRepository.findByTraineeUserUsername(username).stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTrainingsForTrainer(String username, String password) {
        log.info("Fetching trainings for trainer: {}", username);
        userService.authenticate(username, password);

        return trainingRepository.findByTrainerUserUsername(username).stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTrainingsForTrainee(String username, String password,
                                                    LocalDate from, LocalDate to, String trainerName, String trainingType) {
        userService.authenticate(username, password);

        return trainingRepository.findByTraineeUserUsername(username).stream()
                .filter(t -> from == null || !t.getTrainingDate().isBefore(from))
                .filter(t -> to == null || !t.getTrainingDate().isAfter(to))
                .filter(t -> trainerName == null || t.getTrainer().getUser().getFullName().contains(trainerName))
                .filter(t -> trainingType == null || t.getTrainingType().getTrainingTypeName().equalsIgnoreCase(trainingType))
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTrainingsForTrainer(String username, String password,
                                                    LocalDate from, LocalDate to, String traineeName) {
        userService.authenticate(username, password);

        return trainingRepository.findByTrainerUserUsername(username).stream()
                .filter(t -> from == null || !t.getTrainingDate().isBefore(from))
                .filter(t -> to == null || !t.getTrainingDate().isAfter(to))
                .filter(t -> traineeName == null || t.getTrainee().getUser().getFullName().contains(traineeName))
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }


}

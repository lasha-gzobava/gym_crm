package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.CreateTrainingDto;
import org.example.dto.TrainingDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public TrainingDto addTraining(CreateTrainingDto dto) {
        log.info("Adding training: {}", dto.getTrainingName());

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
    public List<TrainingDto> getTrainingsForTrainee(String username) {
        log.info("Fetching trainings for trainee: {}", username);
        return trainingRepository.findByTraineeUserUsername(username).stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingDto> getTrainingsForTrainer(String username) {
        log.info("Fetching trainings for trainer: {}", username);
        return trainingRepository.findByTrainerUserUsername(username).stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }
}

package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.client.WorkloadClient;
import org.example.dto.training.TrainingAddDto;
import org.example.dto.training.TrainingDto;
import org.example.dto.training.TrainingEventRequest;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.mapper.TrainingMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.security.JwtService;
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
    private final WorkloadClient workloadClient;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void addTraining(TrainingAddDto dto, String password) {

        userService.authenticate(dto.getTrainerUsername(), password);
        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        Trainer trainer = trainerRepository.findByUsername(dto.getTrainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(dto.getTrainingName());
        training.setTrainingDate(dto.getTrainingDate());
        training.setTrainingDuration(dto.getTrainingDuration());
        training.setTrainingType(trainer.getSpecialization());


        trainingRepository.save(training);

        TrainingEventRequest event = new TrainingEventRequest();
        event.setUsername(training.getTrainer().getUser().getUsername());
        event.setFirstName(training.getTrainer().getUser().getFirstName());
        event.setLastName(training.getTrainer().getUser().getLastName());
        event.setIsActive(training.getTrainer().getUser().getIsActive());
        event.setTrainingDate(training.getTrainingDate());
        event.setDurationMinutes(training.getTrainingDuration().intValue()); // convert Long → int
        event.setAction(TrainingEventRequest.ActionType.ADD);

        String systemToken = jwtService.generateSystemToken();


        workloadClient.sendEvent(event, systemToken);

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

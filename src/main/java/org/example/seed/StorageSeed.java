package org.example.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.*;
import org.example.entity.*;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageSeed {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeRepository trainingTypeRepository;

    @Value("${seed.file.path}")
    private String seedFilePath;

    @PostConstruct
    public void seedDatabase() {
        log.info("Starting database seeding from file: {}", seedFilePath);

        if (trainingTypeRepository.count() > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            SeedData seedData = objectMapper.readValue(new File(seedFilePath), SeedData.class);

            // Seed Training Types
            for (String name : seedData.getTrainingTypes()) {
                TrainingType type = new TrainingType(name);
                trainingTypeRepository.save(type);
                log.info("Seeded TrainingType: {}", name);
            }

            // Seed Trainees
            for (CreateTraineeDto trainee : seedData.getTrainees()) {
                TraineeDto dto = traineeService.createTrainee(trainee);
                log.info("Seeded Trainee: {}", dto.getUser().getUsername());
            }

            // Seed Trainers
            for (CreateTrainerDto trainer : seedData.getTrainers()) {
                TrainerDto dto = trainerService.createTrainer(trainer);
                log.info("Seeded Trainer: {}", dto.getUser().getUsername());
            }

            // Seed Trainings
            for (CreateTrainingDto training : seedData.getTrainings()) {
                TrainingDto dto = trainingService.addTraining(training);
                log.info("Seeded Training: {}", dto.getTrainingName());
            }

            log.info("Database seeding completed.");

        } catch (IOException e) {
            log.error("Failed to read seed file: {}", seedFilePath, e);
        }
    }

    @Getter
    private static class SeedData {
        private List<String> trainingTypes;
        private List<CreateTraineeDto> trainees;
        private List<CreateTrainerDto> trainers;
        private List<CreateTrainingDto> trainings;
    }
}

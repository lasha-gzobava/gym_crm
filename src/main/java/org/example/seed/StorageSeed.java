package org.example.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class StorageSeed {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;

    @Value("${seed.file.path}")
    private String seedFilePath;


    @Autowired
    public StorageSeed(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userService = userService;
    }





    @PostConstruct
    public void seedDataBase(){
        log.info("Seeding database from file: {} ", seedFilePath);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SeedData seedData = objectMapper.readValue(new File(seedFilePath), SeedData.class);

            // Loading trainees
            for (TraineeSeedData trainee : seedData.getTrainees()){
                Trainee newTrainee = traineeService.createTrainee(
                        trainee.getFirstName(),
                        trainee.getLastName(),
                        trainee.getDateOfBirth(),
                        trainee.getAddress()
                        );
                log.info("Seeded Trainee with: ID: {}", newTrainee.getUserId());
            }

            //Loading trainers
            for (TrainerSeedData trainer : seedData.getTrainers()){
                Trainer newTrainer = trainerService.createTrainer(
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization()
                );
                log.info("Seeded Trainer with: ID: {}", newTrainer.getUserId());
            }

            //LoadingTrainings
            for (TrainingSeedData training : seedData.getTrainings()){
                Training newTraining = trainingService.createTraining(
                        training.getTraineeId(),
                        training.getTrainerId(),
                        training.getTrainingName(),
                        training.getTrainingType(),
                        training.getTrainingDate(),
                        Duration.ofMinutes(training.getTrainingDurationMinutes())
                );
                log.info("Seeded Training: Name={}, Duration={}min, TraineeID={}, TrainerID={}",
                        newTraining.getTrainingName(),
                        training.getTrainingDurationMinutes(),
                        newTraining.getTraineeId(),
                        newTraining.getTrainerId());
            }



        }catch (IOException e){
            log.error("Error reading from file: {}", e.getMessage(), e);
        }


    }

    @Getter
    private static class SeedData {
        private List<TraineeSeedData> trainees;
        private List<TrainerSeedData> trainers;
        private List<TrainingSeedData> trainings;
    }


    @Getter
    private static class TraineeSeedData {
        private String firstName;
        private String lastName;
        private String dateOfBirth;
        private String address;
    }


    @Getter
    private static class TrainingSeedData {
        private Long traineeId;
        private Long trainerId;
        private String trainingName;
        private String trainingType;
        private String trainingDate;
        private int trainingDurationMinutes;
    }

    @Getter
    private static class TrainerSeedData {
        private String firstName;
        private String lastName;
        private String specialization;
    }

}

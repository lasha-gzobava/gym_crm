package org.example;

import org.example.dto.*;
import org.example.entity.TrainingType;
import org.example.facade.GymFacade;
import org.example.repository.TrainingTypeRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.util.List;

public class Demo {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("org.example");
        GymFacade gym = context.getBean(GymFacade.class);

        // --- Seed TrainingType  ---
        TrainingTypeRepository trainingTypeRepository = context.getBean(TrainingTypeRepository.class);
        if (trainingTypeRepository.findByTrainingTypeName("Yoga").isEmpty()) {
            TrainingType yogaType = new TrainingType();
            yogaType.setTrainingTypeName("Yoga");
            trainingTypeRepository.save(yogaType);
            System.out.println("TrainingType 'Yoga' seeded.");
        }
        Long yogaTypeId = trainingTypeRepository.findByTrainingTypeName("Yoga")
                .get().getTrainingTypeId();

        // --- Create Trainee ---
        CreateUserDto traineeUser = new CreateUserDto();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        CreateTraineeDto traineeDto = new CreateTraineeDto();
        traineeDto.setUser(traineeUser);
        traineeDto.setAddress("Main Street 123");
        traineeDto.setDateOfBirth(LocalDate.of(1997, 7, 15));
        TraineeDto createdTrainee = gym.createTrainee(traineeDto);
        System.out.println("Trainee created: " + createdTrainee);

        // --- Create Trainer ---
        CreateUserDto trainerUser = new CreateUserDto();
        trainerUser.setFirstName("Alice");
        trainerUser.setLastName("Smith");
        CreateTrainerDto trainerDto = new CreateTrainerDto();
        trainerDto.setUser(trainerUser);
        trainerDto.setSpecialization("Yoga");
        TrainerDto createdTrainer = gym.createTrainer(trainerDto);
        System.out.println("Trainer created: " + createdTrainer);

        // --- Add Training ---
        CreateTrainingDto trainingDto = new CreateTrainingDto();
        trainingDto.setTrainingName("Morning Yoga");
        trainingDto.setTrainingDate(LocalDate.now());
        trainingDto.setTrainingDuration(60);
        trainingDto.setTrainerId(createdTrainer.getId());
        trainingDto.setTraineeId(createdTrainee.getId());
        trainingDto.setTrainingTypeId(yogaTypeId);
        TrainingDto createdTraining = gym.addTraining(trainingDto);
        System.out.println("Training created: " + createdTraining);

        // --- Fetch trainee and trainer by username ---
        TraineeDto fetchedTrainee = gym.getTrainee(createdTrainee.getUser().getUsername());
        System.out.println("Fetched Trainee: " + fetchedTrainee);

        TrainerDto fetchedTrainer = gym.getTrainer(createdTrainer.getUser().getUsername());
        System.out.println("Fetched Trainer: " + fetchedTrainer);

        // --- List all trainings for trainee and trainer ---
        List<TrainingDto> traineeTrainings = gym.getTraineeTrainings(fetchedTrainee.getUser().getUsername());
        System.out.println("Trainee trainings: " + traineeTrainings);

        List<TrainingDto> trainerTrainings = gym.getTrainerTrainings(fetchedTrainer.getUser().getUsername());
        System.out.println("Trainer trainings: " + trainerTrainings);

        // --- Update trainee info ---
        traineeDto.setAddress("Updated Address 99");
        gym.updateTrainee(fetchedTrainee.getUser().getUsername(), traineeDto);
        System.out.println("Trainee updated.");

        // --- Toggle status ---
        gym.toggleTraineeStatus(fetchedTrainee.getUser().getUsername());
        System.out.println("Trainee status toggled.");

        gym.toggleTrainerStatus(fetchedTrainer.getUser().getUsername());
        System.out.println("Trainer status toggled.");

        // --- Delete trainee and show cascade delete (if configured) ---
        gym.deleteTrainee(fetchedTrainee.getUser().getUsername());
        System.out.println("Trainee deleted (check cascade delete).");

        // --- Try to fetch deleted trainee (should throw) ---
        try {
            gym.getTrainee(fetchedTrainee.getUser().getUsername());
        } catch (Exception e) {
            System.out.println("Expected error (trainee deleted): " + e.getMessage());
        }

        context.close();
    }
}

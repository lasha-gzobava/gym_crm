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
        TrainingTypeRepository typeRepo = context.getBean(TrainingTypeRepository.class);

        // [1] Create Trainee Profile
        CreateUserDto traineeUser = new CreateUserDto("John", "Doe");
        CreateTraineeDto createTrainee = new CreateTraineeDto();
        createTrainee.setUser(traineeUser);
        createTrainee.setAddress("Street 123");
        createTrainee.setDateOfBirth(LocalDate.of(1998, 1, 10));
        TraineeDto trainee = gym.createTrainee(createTrainee);
        String traineeUsername = trainee.getUser().getUsername();
        String traineePassword = "password"; // default generated

        // [2] Create Trainer Profile
        CreateUserDto trainerUser = new CreateUserDto("Alice", "Smith");
        TrainingType yoga = typeRepo.findByTrainingTypeName("Yoga")
                .orElseGet(() -> typeRepo.save(new TrainingType("Yoga")));
        CreateTrainerDto createTrainer = new CreateTrainerDto();
        createTrainer.setUser(trainerUser);
        createTrainer.setSpecialization("Yoga");
        TrainerDto trainer = gym.createTrainer(createTrainer);
        String trainerUsername = trainer.getUser().getUsername();
        String trainerPassword = "password";

        // [3] Authenticate Trainee
        gym.getTrainee(traineeUsername, traineePassword);

        // [4] Authenticate Trainer
        gym.getTrainer(trainerUsername, trainerPassword);

        // [5] Select Trainer Profile
        System.out.println("Trainer profile: " + gym.getTrainer(trainerUsername, trainerPassword));

        // [6] Select Trainee Profile
        System.out.println("Trainee profile: " + gym.getTrainee(traineeUsername, traineePassword));

        // [7] Change Trainee Password
        PasswordChangeDto changeTraineePass = new PasswordChangeDto(traineeUsername, "password", "newpass123");
        gym.changeTraineePassword(changeTraineePass);
        traineePassword = "newpass123";

        // [8] Change Trainer Password
        PasswordChangeDto changeTrainerPass = new PasswordChangeDto(trainerUsername, "password", "trainerpass");
        gym.changeTrainerPassword(changeTrainerPass);
        trainerPassword = "trainerpass";

        // [9] Update Trainer Profile
        createTrainer.setSpecialization("Yoga");
        gym.updateTrainer(trainerUsername, trainerPassword, createTrainer);

        // [10] Update Trainee Profile
        createTrainee.setAddress("Updated Ave 456");
        gym.updateTrainee(traineeUsername, traineePassword, createTrainee);

        // [11] Deactivate/Activate Trainee
        gym.toggleTraineeStatus(traineeUsername, traineePassword);

        // [12] Deactivate/Activate Trainer
        gym.toggleTrainerStatus(trainerUsername, trainerPassword);

        // [13] Delete Trainee Profile
        gym.deleteTrainee(traineeUsername, traineePassword);

        // Recreate Trainee for remaining steps
        trainee = gym.createTrainee(createTrainee);
        traineeUsername = trainee.getUser().getUsername();
        traineePassword = "password";

        // [14] Get Trainee Trainings with Filters
        List<TrainingDto> filteredTraineeTrainings = gym.getTraineeTrainingsFiltered(
                traineeUsername,
                traineePassword,
                LocalDate.now().minusDays(7),
                LocalDate.now().plusDays(1),
                trainer.getUser().getFullName(),
                "Yoga"
        );
        System.out.println("[14] Filtered Trainee Trainings: " + filteredTraineeTrainings);

        // [15] Get Trainer Trainings with Filters
        List<TrainingDto> filteredTrainerTrainings = gym.getTrainerTrainingsFiltered(
                trainerUsername,
                trainerPassword,
                LocalDate.now().minusDays(7),
                LocalDate.now().plusDays(1),
                trainee.getUser().getFullName()
        );
        System.out.println("[15] Filtered Trainer Trainings: " + filteredTrainerTrainings);

        // [16] Add Training
        CreateTrainingDto training = new CreateTrainingDto();
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        training.setTrainerId(trainer.getId());
        training.setTraineeId(trainee.getId());
        training.setTrainingTypeId(yoga.getTrainingTypeId());
        training.setTrainerUsername(trainerUsername);
        training.setTrainerPassword(trainerPassword);

        gym.addTraining(training, trainerUsername, trainerPassword);

        // Optional check: see training
        List<TrainingDto> trTrainings = gym.getTrainerTrainings(trainerUsername, trainerPassword);
        System.out.println("Trainer trainings: " + trTrainings);

        // [17] Get Unassigned Trainers for Trainee
        List<TrainerDto> unassigned = gym.getUnassignedTrainers(traineeUsername, traineePassword);
        System.out.println("[17] Unassigned Trainers: " + unassigned);

        // [18] Update Trainee's Trainers List
        gym.updateTraineeTrainers(traineeUsername, traineePassword, List.of(trainer.getId()));
        System.out.println("[18] Updated trainee-trainers mapping complete.");

        context.close();
    }
}

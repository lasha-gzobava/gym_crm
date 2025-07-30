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

        // 1-2. Create profiles
        CreateUserDto traineeUser = new CreateUserDto("John", "Doe");
        CreateTraineeDto createTrainee = new CreateTraineeDto();
        createTrainee.setUser(traineeUser);
        createTrainee.setAddress("Street 123");
        createTrainee.setDateOfBirth(LocalDate.of(1998, 1, 10));
        TraineeDto trainee = gym.createTrainee(createTrainee);
        String traineeUsername = trainee.getUser().getUsername();
        String traineePassword = "password"; // default

        CreateUserDto trainerUser = new CreateUserDto("Alice", "Smith");
        TrainingType yoga = typeRepo.findByTrainingTypeName("Yoga")
                .orElseGet(() -> typeRepo.save(new TrainingType("Yoga")));
        CreateTrainerDto createTrainer = new CreateTrainerDto();
        createTrainer.setUser(trainerUser);
        createTrainer.setSpecialization("Yoga");
        TrainerDto trainer = gym.createTrainer(createTrainer);
        String trainerUsername = trainer.getUser().getUsername();
        String trainerPassword = "password";

        // 3-4. Username/password check (via get calls)
        gym.getTrainee(traineeUsername, traineePassword);
        gym.getTrainer(trainerUsername, trainerPassword);

        // 5-6. Select profiles
        System.out.println("Trainee profile: " + gym.getTrainee(traineeUsername, traineePassword));
        System.out.println("Trainer profile: " + gym.getTrainer(trainerUsername, trainerPassword));

        // 7-8. Change password
        PasswordChangeDto changeTraineePass = new PasswordChangeDto(traineeUsername, "password", "newpass123");
        gym.changeTraineePassword(changeTraineePass);
        traineePassword = "newpass123";

        PasswordChangeDto changeTrainerPass = new PasswordChangeDto(trainerUsername, "password", "trainerpass");
        gym.changeTrainerPassword(changeTrainerPass);
        trainerPassword = "trainerpass";

        // 9-10. Update profiles
        createTrainee.setAddress("Updated Ave 456");
        gym.updateTrainee(traineeUsername, traineePassword, createTrainee);

        createTrainer.setSpecialization("Yoga");
        gym.updateTrainer(trainerUsername, trainerPassword, createTrainer);

        // 11-12. Toggle status
        gym.toggleTraineeStatus(traineeUsername, traineePassword);
        gym.toggleTrainerStatus(trainerUsername, trainerPassword);

        // 13. Delete trainee (but first create again for remaining steps)
        gym.deleteTrainee(traineeUsername, traineePassword);

        trainee = gym.createTrainee(createTrainee);
        traineeUsername = trainee.getUser().getUsername();
        traineePassword = "password";

        // 16. Add training
        CreateTrainingDto training = new CreateTrainingDto();
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);
        training.setTrainerId(trainer.getId());
        training.setTraineeId(trainee.getId());
        training.setTrainingTypeId(yoga.getTrainingTypeId());
        gym.addTraining(training, trainerUsername, trainerPassword);

        // 14-15. Get trainings with criteria (for now just general list)
        List<TrainingDto> tTrainings = gym.getTraineeTrainings(traineeUsername, traineePassword);
        System.out.println("Trainee trainings: " + tTrainings);

        List<TrainingDto> trTrainings = gym.getTrainerTrainings(trainerUsername, trainerPassword);
        System.out.println("Trainer trainings: " + trTrainings);

        // 17. Get trainers not assigned to trainee
        System.out.println("Unassigned trainers: " + gym.getUnassignedTrainers(traineeUsername, traineePassword));

        // 18. Update assigned trainers
        gym.updateTraineeTrainers(traineeUsername, traineePassword, List.of(trainer.getId()));

        System.out.println("Updated trainee-trainers mapping complete.");

        context.close();
    }
}

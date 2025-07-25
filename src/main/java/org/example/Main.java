package org.example;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.facade.GymFacadeService;
import org.example.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Duration;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        GymFacadeService gym = context.getBean(GymFacadeService.class);

        System.out.println("=== DEMO START ===");

        // 1. Register Trainee
        Trainee t1 = gym.registerTrainee("John", "Doe", "1995-06-20", "Rustavi");
        System.out.println("Created trainee: " + t1);

        // 2. Register Trainer
        Trainer r1 = gym.registerTrainer("Anna", "Smith", "Yoga");
        System.out.println("Created trainer: " + r1);

        // 3. Schedule Training
        Training training = gym.scheduleTraining(
                t1.getId(), r1.getId(),
                "Morning Yoga", "Wellness",
                "2025-07-30", Duration.ofMinutes(60)
        );
        System.out.println("Scheduled training: " + training);

        // 4. Read all trainees
        List<Trainee> trainees = gym.getAllTrainees();
        System.out.println("All trainees: " + trainees);

        // 5. Read all trainers
        List<Trainer> trainers = gym.getAllTrainers();
        System.out.println("All trainers: " + trainers);

        // 6. Read all trainings
        List<Training> trainings = gym.getAllTrainings();
        System.out.println("All trainings: " + trainings);

        // 7. Attempt update (valid)
        t1.setAddress("Tbilisi");
        gym.updateTrainee(t1);
        System.out.println("Updated trainee: " + t1);

        // 8. Attempt update (invalid ID)
        Trainee t2 = new Trainee(999L, "2000-01-01", "Batumi", 999L);
        gym.updateTrainee(t2); // Should trigger a warning or no-op

        // 9. Attempt delete
        gym.deleteTraining(training.getId());
        System.out.println("Deleted training ID: " + training.getId());

        // 10. Print after deletion
        System.out.println("Trainings after deletion: " + gym.getAllTrainings());

        System.out.println("=== DEMO END ===");
    }
}

package org.example.facade;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;

import java.time.Duration;
import java.util.List;

public interface GymFacadeService {

    // Create
    Trainee registerTrainee(String firstName, String lastName, String dob, String address);
    Trainer registerTrainer(String firstName, String lastName, String specialization);
    Training scheduleTraining(Long traineeId, Long trainerId, String name, String type, String date, Duration duration);

    // Read
    List<Trainee> getAllTrainees();
    List<Trainer> getAllTrainers();
    List<Training> getAllTrainings();

    // Update
    void updateTrainee(Trainee trainee);
    void updateTrainer(Trainer trainer);
    void updateTraining(Training training);

    // Delete
    void deleteTrainee(Long id);
    void deleteTrainer(Long id);
    void deleteTraining(Long id);
}

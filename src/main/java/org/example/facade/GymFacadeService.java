package org.example.facade;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;

import java.util.List;

public interface GymFacadeService {
    Trainee registerTrainee(String firstName, String lastName, String dob, String address);
    Trainer registerTrainer(String firstName, String lastName, String specialization);
    Training scheduleTraining(Long traineeId, Long trainerId, String name, String type, String date, String duration);

    List<Trainee> getAllTrainees();
    List<Trainer> getAllTrainers();
    List<Training> getAllTrainings();
}

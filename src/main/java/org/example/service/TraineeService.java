package org.example.service;

import org.example.entity.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee createTrainee(String firstName, String lastName, String dateOfBirth, String address);
    void updateTrainee(Trainee trainee);
    void deleteTrainee(Long id);
    Trainee getTraineeById(Long id); //Selecting profile
    List<Trainee> getAllTrainee();
}

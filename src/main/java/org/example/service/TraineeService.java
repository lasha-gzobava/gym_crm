package org.example.service;

import org.example.dto.trainee.*;
import org.example.dto.login.PasswordChangeDto;
import org.example.entity.Trainee;

import java.util.List;

public interface TraineeService {


    TraineeDto createTrainee(CreateTraineeDto dto);
    TraineeCredentialsDto registerWithCredentials(CreateTraineeDto dto);
    TraineeDto getByUsername(String username, String password);
    TraineeProfileDto getTraineeProfile(String username);
    TraineeProfileDto updateProfile(TraineeProfileUpdateDto dto);
    void updateTrainee(String username, CreateTraineeDto dto, String password);
    void deleteByUsername(String username, String password);
    void toggleActive(String username, String password);
    void changePassword(PasswordChangeDto dto);
    void updateAssignedTrainers(String traineeUsername, List<Long> trainerIds);
    void updateTrainersList(String traineeUsername, List<Long> trainerIds);
}

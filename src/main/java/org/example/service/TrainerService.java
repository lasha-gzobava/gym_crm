package org.example.service;

import org.example.dto.trainee.CreateTraineeDto;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.CreateTrainerDto;
import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainer.TrainerDto;

import java.util.List;

public interface TrainerService {
    TrainerDto createTrainer(CreateTrainerDto dto);
    TraineeCredentialsDto registerWithCredentials(CreateTrainerDto dto);
    TrainerDto getByUsername(String username, String password);
    void changePassword(PasswordChangeDto dto);
    void updateTrainer(String username, CreateTrainerDto dto, String password);
    void toggleActive(String username, String password);
    void deleteByUsername(String username, String password);
    List<TrainerDto> getUnassignedTrainersForTrainee(String traineeUsername);
}

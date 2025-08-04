package org.example.service;

import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainee.*;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.training.TraineeTrainingRequestDto;
import org.example.dto.training.TraineeTrainingResponseDto;

import java.util.List;

public interface TraineeService {
    TraineeCredentialsDto registerWithCredentials(TraineeCreateDto dto);

    TraineeProfileDto getTraineeProfile(String username, String password);

    TraineeProfileDto updateProfile(TraineeProfileUpdateDto dto, String password);

    void deleteByUsername(String username, String password);

    void toggleActive(String username, boolean isActive, String password);

    List<TrainerForTrainerListDto> updateTraineeTrainers(TraineeTrainerUpdateDto dto, String password);

    List<TraineeTrainingResponseDto> getTraineeTrainingsList(TraineeTrainingRequestDto dto, String password);
}

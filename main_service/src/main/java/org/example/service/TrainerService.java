package org.example.service;

import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.TrainerCreateDto;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.trainer.TrainerProfileDto;
import org.example.dto.trainer.TrainerUpdateDto;
import org.example.dto.training.TrainerTrainingRequestDto;
import org.example.dto.training.TrainerTrainingResponseDto;

import java.util.List;

public interface TrainerService {
    TraineeCredentialsDto registerWithCredentials(TrainerCreateDto dto);
    TrainerProfileDto getTrainerProfile(String username, String password);
    TrainerProfileDto updateTrainerProfile(TrainerUpdateDto dto, String password);
    boolean toggleActive(String username, boolean isActive, String password);
    List<TrainerForTrainerListDto> getUnassignedTrainersForTrainee(String traineeUsername, String password);
    List<TrainerTrainingResponseDto> getTrainerTrainingsList(TrainerTrainingRequestDto dto, String password);
}

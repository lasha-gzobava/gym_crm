package org.example.service;

import org.example.dto.CreateTrainingDto;
import org.example.dto.TrainingDto;

import java.util.List;

public interface TrainingService {

    TrainingDto addTraining(CreateTrainingDto dto);
    List<TrainingDto> getTrainingsForTrainee(String username);
    List<TrainingDto> getTrainingsForTrainer(String username);
}

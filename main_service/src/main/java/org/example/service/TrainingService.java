package org.example.service;

import org.example.dto.training.TrainingAddDto;
import org.example.dto.training.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {

    boolean addTraining(TrainingAddDto dto, String password);


    List<TrainingDto> getTrainingsForTrainee(String username, String password);

    List<TrainingDto> getTrainingsForTrainer(String username, String password);

    List<TrainingDto> getTrainingsForTrainee(String username, String password,
                                             LocalDate fromDate, LocalDate toDate,
                                             String trainerName, String trainingType);

    List<TrainingDto> getTrainingsForTrainer(String username, String password,
                                             LocalDate fromDate, LocalDate toDate,
                                             String traineeName);
}

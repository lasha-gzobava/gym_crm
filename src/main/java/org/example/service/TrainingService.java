package org.example.service;

import org.example.entity.Training;

import java.time.Duration;
import java.util.List;

public interface TrainingService {
    Training createTraining(Long traineeId, Long trainerId, String trainingName,
                            String trainingType, String trainingDate, Duration trainingDuration);

    void updateTraining(Training training);

    void deleteTraining(Long id);

    Training getTrainingById(Long id);

    List<Training> getAllTrainings();
}

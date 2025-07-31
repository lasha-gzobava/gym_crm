package org.example.mapper;

import org.example.dto.training.TrainingDto;
import org.example.entity.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public TrainingDto toDto(Training training) {
        if (training == null) return null;

        TrainingDto dto = new TrainingDto();
        dto.setId(training.getTrainingId());
        dto.setTrainingName(training.getTrainingName());
        dto.setTrainingDate(training.getTrainingDate());
        dto.setTrainingDuration(Math.toIntExact(training.getTrainingDuration()));
        dto.setTrainerId(training.getTrainer().getTrainerId());
        dto.setTraineeId(training.getTrainee().getTraineeId());
        dto.setTrainingTypeId(training.getTrainingType().getTrainingTypeId());

        return dto;
    }
}

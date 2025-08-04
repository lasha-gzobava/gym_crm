package org.example.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TrainerTrainingResponseDto {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Long trainingDuration;
    private String traineeName;
}

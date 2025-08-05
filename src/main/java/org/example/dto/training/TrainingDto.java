package org.example.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDto {

    private Long id;
    private String trainingName;
    private LocalDate trainingDate;
    private int trainingDuration;
    private Long trainerId;
    private Long traineeId;
    private Long trainingTypeId;
}

package org.example.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TrainingDto {

    private Long id;
    private String trainingName;
    private LocalDate trainingDate;
    private int trainingDuration;
    private Long trainerId;
    private Long traineeId;
    private Long trainingTypeId;
}

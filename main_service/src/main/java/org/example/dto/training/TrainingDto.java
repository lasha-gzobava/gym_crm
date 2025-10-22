package org.example.dto.training;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDto {

    private Long id;

    @NotBlank(message = "Training name is required")
    private String trainingName;

    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;

    @Min(value = 1, message = "Training duration must be at least 1 minute")
    private int trainingDuration;

    @NotNull(message = "Trainer ID is required")
    private Long trainerId;

    @NotNull(message = "Trainee ID is required")
    private Long traineeId;

    @NotNull(message = "Training type ID is required")
    private Long trainingTypeId;
}

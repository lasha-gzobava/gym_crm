package org.example.dto.training;


import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateTrainingDto {

    @NotBlank(message = "Training name must not be empty.")
    private String trainingName;

    @NotNull(message = "Training date is required.")
    private LocalDate trainingDate;

    @Positive(message = "Training duration must be a positive number.")
    private int trainingDuration;

    @NotNull(message = "Trainer ID must be provided.")
    private Long trainerId;

    @NotNull(message = "Trainee ID must be provided.")
    private Long traineeId;

    @NotNull(message = "Training Type ID must be provided.")
    private Long trainingTypeId;

    @NotBlank(message = "Trainer username must be provided.")
    private String trainerUsername;

    @NotBlank(message = "Trainer password must be provided.")
    private String trainerPassword;

}

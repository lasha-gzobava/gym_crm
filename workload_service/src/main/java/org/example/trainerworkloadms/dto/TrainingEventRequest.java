package org.example.trainerworkloadms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrainingEventRequest {
    @NotBlank(message = "Username cannot be blank")
    String username;

    @NotBlank(message = "First name cannot be blank")
    String firstName;

    @NotBlank(message = "Last name cannot be blank")
    String lastName;

    Boolean isActive;

    @NotNull(message = "Training date cannot be blank")
    LocalDate trainingDate;
    @Positive(message = "Duration must be positive")
    int durationMinutes;
    @NotNull(message = "Action cannot be null")
    ActionType action;

    public enum ActionType { ADD, DELETE }

}

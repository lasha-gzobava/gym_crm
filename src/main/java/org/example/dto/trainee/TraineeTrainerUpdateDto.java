package org.example.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class TraineeTrainerUpdateDto {

    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @NotEmpty(message = "Trainer usernames list must not be empty")
    private List<@NotBlank(message = "Trainer username cannot be blank") String> trainerUsernames;
}

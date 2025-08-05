package org.example.dto.training;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeTrainingRequestDto {
    @NotBlank(message = "Username is required")
    private String username;
    private LocalDate periodFrom; //optional
    private LocalDate periodTo; //optional
    private String trainerName; //optional
    private String trainingType; //optional


}

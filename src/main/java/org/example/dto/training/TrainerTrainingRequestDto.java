package org.example.dto.training;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TrainerTrainingRequestDto {
    @NotBlank(message = "Username is required")
    private String username;
    private LocalDate periodFrom; // optional
    private LocalDate periodTo;   // optional
    private String traineeName;   // optional
}

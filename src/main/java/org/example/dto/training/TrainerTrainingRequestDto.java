package org.example.dto.training;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TrainerTrainingRequestDto {
    private String username; // required
    private LocalDate periodFrom; // optional
    private LocalDate periodTo;   // optional
    private String traineeName;   // optional
}

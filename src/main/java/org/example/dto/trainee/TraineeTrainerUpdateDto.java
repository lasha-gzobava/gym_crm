package org.example.dto.trainee;

import lombok.Data;

import java.util.List;

@Data
public class TraineeTrainerUpdateDto {
    private String traineeUsername;
    private List<String> trainerUsernames;
}

package org.example.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dto.trainee.TraineeInfoDto;

import java.util.List;

@Data
@AllArgsConstructor
public class TrainerProfileDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeInfoDto> trainees;
}


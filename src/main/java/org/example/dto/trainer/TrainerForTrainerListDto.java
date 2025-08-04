package org.example.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TrainerForTrainerListDto {
    private String userName;
    private String firstName;
    private String lastName;
    private String specialization;
}

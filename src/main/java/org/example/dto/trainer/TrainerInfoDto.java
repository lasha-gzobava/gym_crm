package org.example.dto.trainer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerInfoDto {
    private String username;
    private String firstName;
    private String lastName;
    private String specialization; // trainingType name
}

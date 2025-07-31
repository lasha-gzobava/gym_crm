package org.example.dto.trainee;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TraineeProfileUpdateDto {
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth; // optional
    private String address;        // optional
    private Boolean isActive;
}

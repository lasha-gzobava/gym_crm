package org.example.dto;


import jakarta.validation.Valid;
import lombok.Data;


import java.time.LocalDate;

@Data
public class CreateTraineeDto {
    private String address;
    private LocalDate dateOfBirth;

    @Valid
    private CreateUserDto user;
}

package org.example.dto.trainee;


import jakarta.validation.Valid;
import lombok.Data;
import org.example.dto.user.CreateUserDto;


import java.time.LocalDate;

@Data
public class CreateTraineeDto {
    private String address;
    private LocalDate dateOfBirth;

    @Valid
    private CreateUserDto user;
}

package org.example.dto.trainee;


import jakarta.validation.Valid;
import lombok.Data;
import org.example.dto.user.UserCreateDto;


import java.time.LocalDate;

@Data
public class TraineeCreateDto {
    private String address;
    private LocalDate dateOfBirth;

    @Valid
    private UserCreateDto user;
}

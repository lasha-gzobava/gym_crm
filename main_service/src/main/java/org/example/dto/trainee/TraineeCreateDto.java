package org.example.dto.trainee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.dto.user.UserCreateDto;

import java.time.LocalDate;

@Data
public class TraineeCreateDto {
    private String address;
    private LocalDate dateOfBirth;

    @Valid
    @NotNull(message = "User information must be provided")
    private UserCreateDto user;
}

package org.example.dto.trainer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.dto.user.CreateUserDto;

@Data
public class CreateTrainerDto {

    @NotBlank(message = "Specialization is required.")
    private String specialization;

    @Valid
    private CreateUserDto user;
}

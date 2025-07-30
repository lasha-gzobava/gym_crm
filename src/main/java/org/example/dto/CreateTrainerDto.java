package org.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTrainerDto {

    @NotBlank(message = "Specialization is required.")
    private String specialization;

    @Valid
    private CreateUserDto user;
}

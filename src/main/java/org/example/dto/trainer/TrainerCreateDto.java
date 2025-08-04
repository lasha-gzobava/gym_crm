package org.example.dto.trainer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dto.user.UserCreateDto;

@Data
@AllArgsConstructor
public class TrainerCreateDto {
    private String firstName;
    private String lastName;
    private String specialization;
}

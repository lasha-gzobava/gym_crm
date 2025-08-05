package org.example.dto.trainer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.user.UserCreateDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerCreateDto {
    private String firstName;
    private String lastName;
    private String specialization;
}

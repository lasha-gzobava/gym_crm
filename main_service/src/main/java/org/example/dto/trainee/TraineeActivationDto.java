package org.example.dto.trainee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeActivationDto {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotNull(message = "isActive status must be provided")
    private Boolean isActive;
}

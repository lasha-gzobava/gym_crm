package org.example.dto.trainee;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeDeleteRequestDto {
    private String username;
    private String password;
}

package org.example.dto.trainee;


import lombok.Data;
import org.example.dto.user.UserDto;

import java.time.LocalDate;

@Data
public class TraineeDto {
    private Long id;
    private String address;
    private LocalDate dateOfBirth;
    private UserDto user;
}

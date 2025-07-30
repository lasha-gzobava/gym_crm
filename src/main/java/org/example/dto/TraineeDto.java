package org.example.dto;


import lombok.Data;
import java.time.LocalDate;

@Data
public class TraineeDto {
    private Long id;
    private String address;
    private LocalDate dateOfBirth;
    private UserDto user;
}

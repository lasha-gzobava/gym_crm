package org.example.dto.trainer;


import lombok.Data;
import org.example.dto.user.UserDto;

@Data
public class TrainerDto {
    private Long id;
    private String specialization;
    private UserDto user;
}

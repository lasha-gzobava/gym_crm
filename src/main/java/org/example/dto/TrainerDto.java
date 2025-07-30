package org.example.dto;


import lombok.Data;

@Data
public class TrainerDto {
    private Long id;
    private String specialization;
    private UserDto user;
}

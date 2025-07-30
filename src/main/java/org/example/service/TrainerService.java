package org.example.service;

import org.example.dto.CreateTrainerDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.TrainerDto;

import java.util.List;

public interface TrainerService {
    TrainerDto createTrainer(CreateTrainerDto dto);
    TrainerDto getByUsername(String username, String password);
    void changePassword(PasswordChangeDto dto);
    void updateTrainer(String username, CreateTrainerDto dto, String password);
    void toggleActive(String username, String password);
    void deleteByUsername(String username, String password);
    List<TrainerDto> getUnassignedTrainersForTrainee(String traineeUsername);
}

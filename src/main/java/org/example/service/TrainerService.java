package org.example.service;

import org.example.dto.CreateTrainerDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.TrainerDto;

public interface TrainerService {

    TrainerDto createTrainer(CreateTrainerDto dto);
    TrainerDto getByUsername(String username);
    void changePassword(PasswordChangeDto dto);
    void updateTrainer(String username, CreateTrainerDto dto);
    void toggleActive(String username);
    void deleteByUsername(String username);
}

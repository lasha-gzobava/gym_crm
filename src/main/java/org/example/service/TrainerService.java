package org.example.service;

import org.example.entity.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer createTrainer(String fistName, String lastName, String specialization);
    void updateTrainer(Trainer trainer);
    void deleteTrainer(Long id);
    Trainer getTrainerById(Long id); //selecting trainer
    List<Trainer> getAllTrainer();

}

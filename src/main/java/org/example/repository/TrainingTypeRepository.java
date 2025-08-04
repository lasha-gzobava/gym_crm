package org.example.repository;

import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);


}
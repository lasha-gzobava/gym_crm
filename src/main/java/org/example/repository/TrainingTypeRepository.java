package org.example.repository;

import org.example.entity.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);


}
package org.example.repository;



import org.example.entity.Trainee;
import org.example.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findByTraineeUserUsername(String username);
    Optional<Training> findByTrainerUserUsername(String username);
    void deleteAllByTrainee(Trainee trainee);
    List<Training> findAllByTrainee(Trainee trainee);
}
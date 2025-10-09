package org.example.trainerworkloadms.repository;

import jakarta.validation.constraints.NotBlank;
import org.example.trainerworkloadms.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(@NotBlank(message = "Username cannot be blank") String username);
}

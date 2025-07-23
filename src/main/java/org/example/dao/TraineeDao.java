package org.example.dao;

import org.example.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee save(Trainee trainee);
    Optional<Trainee> update(Trainee trainee);
    void delete(Long id);
    Trainee getById(Long id);
    List<Trainee> getAll();
}

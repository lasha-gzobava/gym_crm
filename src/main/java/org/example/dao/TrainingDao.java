package org.example.dao;

import org.example.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {
    Training save(Training training);
    Optional<Training> update(Training training);
    void delete(Long id);
    Training getById(Long id);
    List<Training> getAll();
}

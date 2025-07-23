package org.example.dao;

import org.example.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer save(Trainer trainer);
    Optional<Trainer> update(Trainer trainer);
    void delete(Long id);
    Trainer getById(Long id);
    List<Trainer> getAll();
}

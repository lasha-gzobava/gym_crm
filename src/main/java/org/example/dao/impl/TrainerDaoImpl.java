package org.example.dao.impl;

import org.example.dao.TrainerDao;
import org.example.entity.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDaoImpl.class);
    private Map<Long, Trainer> trainerStorage;


    @Autowired
    public void setTraineeStorage(Map<Long, Trainer> traineeStorage) {
        this.trainerStorage = traineeStorage;
    }

    @Override
    public Trainer save(Trainer trainer) {
        if (trainerStorage.containsKey(trainer.getId())) {
            throw new IllegalArgumentException("Trainer already exists with ID: " + trainer.getId());
        }
        trainerStorage.put(trainer.getId(), trainer);
        logger.info("Trainer saved with ID: {}", trainer.getId());
        return trainer;
    }

    @Override
    public Optional<Trainer> update(Trainer trainer) {
        if (!trainerStorage.containsKey(trainer.getId())) {
            return Optional.empty();
        }
        trainerStorage.put(trainer.getId(), trainer);
        logger.info("Trainer updated with ID: {}", trainer.getId());
        return Optional.of(trainer);
    }

    @Override
    public void delete(Long id) {
        trainerStorage.remove(id);
        logger.info("Trainer deleted with ID: {}", id);
    }

    @Override
    public Trainer getById(Long id) {
        return trainerStorage.get(id);
    }

    @Override
    public List<Trainer> getAll() {
        return new ArrayList<>(trainerStorage.values());
    }
}

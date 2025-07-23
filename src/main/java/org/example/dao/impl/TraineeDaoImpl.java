package org.example.dao.impl;

import org.example.dao.TraineeDao;
import org.example.entity.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDaoImpl.class);

    private Map<Long, Trainee> traineeStorage;

    @Autowired
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Trainee save(Trainee trainee) {
        if (traineeStorage.containsKey(trainee.getId())) {
            logger.error("Save failed: Trainee already exists with ID: {}", trainee.getId());
            throw new IllegalArgumentException("Trainee already exists with ID: " + trainee.getId());
        }
        traineeStorage.put(trainee.getId(), trainee);
        logger.info("Trainee saved: {}", trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> update(Trainee trainee) {
        if (!traineeStorage.containsKey(trainee.getId())) {
            logger.warn("Update failed: Trainee not found with ID: {}", trainee.getId());
            return Optional.empty();
        }
        traineeStorage.put(trainee.getId(), trainee);
        logger.info("Trainee updated: {}", trainee);
        return Optional.of(trainee);
    }

    @Override
    public void delete(Long id) {
        if (traineeStorage.remove(id) != null) {
            logger.info("Trainee deleted with ID: {}", id);
        } else {
            logger.warn("Delete failed: No trainee found with ID: {}", id);
        }
    }

    @Override
    public Trainee getById(Long id) {
        Trainee trainee = traineeStorage.get(id);
        if (trainee != null) {
            logger.info("Fetched trainee with ID: {}", id);
        } else {
            logger.warn("No trainee found with ID: {}", id);
        }
        return trainee;
    }

    @Override
    public List<Trainee> getAll() {
        List<Trainee> list = new ArrayList<>(traineeStorage.values());
        logger.info("Fetched all trainees: count={}", list.size());
        return list;
    }
}

package org.example.dao.impl;

import lombok.Setter;
import org.example.dao.TrainingDao;
import org.example.entity.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainingDaoImpl implements TrainingDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDaoImpl.class);


    @Autowired
    private Map<Long, Training> trainingStorage;


    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Training save(Training training) {
        if (trainingStorage.containsKey(training.getId())) {
            throw new IllegalArgumentException("Training already exists with ID: " + training.getId());
        }
        trainingStorage.put(training.getId(), training);
        logger.info("Training saved with ID: {}", training.getId());
        return training;
    }

    @Override
    public Optional<Training> update(Training training) {
        if (!trainingStorage.containsKey(training.getId())) return Optional.empty();
        trainingStorage.put(training.getId(), training);
        logger.info("Training updated with ID: {}", training.getId());
        return Optional.of(training);
    }

    @Override
    public void delete(Long id) {
        trainingStorage.remove(id);
        logger.info("Training deleted with ID: {}", id);
    }

    @Override
    public Training getById(Long id) {
        return trainingStorage.get(id);
    }

    @Override
    public List<Training> getAll() {
        return new ArrayList<>(trainingStorage.values());
    }
}

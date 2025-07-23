package org.example.service.impl;

import org.example.dao.TrainingDao;
import org.example.entity.Training;
import org.example.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao;
    private final AtomicLong idGenerator = new AtomicLong();

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Training createTraining(Long traineeId, Long trainerId, String trainingName,
                                   String trainingType, String trainingDate, String trainingDuration) {
        Long id = idGenerator.incrementAndGet();
        Training training = new Training(id, traineeId, trainerId, trainingName,
                trainingType, trainingDate, trainingDuration);

        trainingDao.save(training);
        logger.info("Training created: ID={}, Name={}, TraineeID={}, TrainerID={}",
                id, trainingName, traineeId, trainerId);
        return training;
    }

    @Override
    public void updateTraining(Training training) {
        if (trainingDao.update(training).isEmpty()) {
            logger.error("Update failed: Training not found with ID: {}", training.getId());
            throw new NoSuchElementException("Training not found with ID: " + training.getId());
        }
        logger.info("Training updated: ID={}", training.getId());
    }

    @Override
    public void deleteTraining(Long id) {
        if (trainingDao.getById(id) == null) {
            logger.error("Delete failed: Training not found with ID: {}", id);
            throw new NoSuchElementException("Training not found with ID: " + id);
        }
        trainingDao.delete(id);
        logger.warn("Training deleted: ID={}", id);
    }

    @Override
    public Training getTrainingById(Long id) {
        Training training = trainingDao.getById(id);
        if (training == null) {
            logger.warn("Training not found for ID: {}", id);
            throw new NoSuchElementException("Training not found with ID: " + id);
        }
        return training;
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingDao.getAll();
    }
}
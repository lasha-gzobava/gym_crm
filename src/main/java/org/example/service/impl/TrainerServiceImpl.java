package org.example.service.impl;

import org.example.dao.TrainerDao;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.example.service.TrainerService;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;
    private UserService userService;
    private final AtomicLong idGenerator = new AtomicLong();

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainer createTrainer(String firstName, String lastName, String specialization) {
        logger.info("Creating new trainer: {}, {}", firstName, lastName);

        User user = userService.createUser(firstName, lastName);
        Long id = idGenerator.incrementAndGet();

        Trainer trainer = new Trainer(id, specialization, user.getId());
        trainerDao.save(trainer);

        logger.info("Trainer created: ID: {}, UserID: {}", id, user.getId());
        return trainer;
    }

    @Override
    public void updateTrainer(Trainer trainer) {
        Optional<Trainer> updated = trainerDao.update(trainer);
        if (updated.isEmpty()) {
            logger.error("Update failed: Trainer not found with ID: {}", trainer.getId());
            throw new NoSuchElementException("Trainer not found with ID: " + trainer.getId());
        }
    }

    @Override
    public void deleteTrainer(Long id) {
        if (trainerDao.getById(id) == null) {
            logger.error("Delete failed: Trainer not found with ID: {}", id);
            throw new NoSuchElementException("Trainer not found with ID: " + id);
        }
        trainerDao.delete(id);
        logger.warn("Trainer removed with ID: {}", id);
    }

    @Override
    public Trainer getTrainerById(Long id) {
        Trainer trainer = trainerDao.getById(id);
        if (trainer == null) {
            logger.warn("Trainer not found for ID: {}", id);
            throw new NoSuchElementException("Trainer not found with ID: " + id);
        }
        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainer() {
        return trainerDao.getAll();
    }
}

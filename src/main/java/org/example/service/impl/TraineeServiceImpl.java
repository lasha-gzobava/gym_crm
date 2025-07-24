package org.example.service.impl;

import org.example.dao.TraineeDao;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.example.service.TraineeService;
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
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;
    private UserService userService;
    private final AtomicLong idGenerator = new AtomicLong();


    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Trainee createTrainee(String firstName, String lastName, String dateOfBirth, String address) {
        logger.info("Creating new trainee: {} {}", firstName, lastName);

        User user = userService.createUser(firstName, lastName);
        Long id = idGenerator.incrementAndGet();

        Trainee trainee = new Trainee(id, dateOfBirth, address, user.getId());
        traineeDao.save(trainee);

        logger.info("Trainee created: ID={}, UserID={}", id, user.getId());
        return trainee;
    }

    @Override
    public void updateTrainee(Trainee trainee) {
        Optional<Trainee> updated = traineeDao.update(trainee);
        if (updated.isEmpty()) {
            logger.error("Update failed: Trainee not found with ID: {}", trainee.getId());
            throw new NoSuchElementException("Trainee not found with ID: " + trainee.getId());
        }
        logger.info("Trainee updated with ID: {}", trainee.getId());
    }

    @Override
    public void deleteTrainee(Long id) {
        if (traineeDao.getById(id) == null) {
            logger.error("Delete failed: Trainee not found with ID: {}", id);
            throw new NoSuchElementException("Trainee not found with ID: " + id);
        }
        traineeDao.delete(id);
        logger.warn("Trainee removed with ID: {}", id);
    }

    @Override
    public Trainee getTraineeById(Long id) {
        Trainee trainee = traineeDao.getById(id);
        if (trainee == null) {
            logger.warn("Trainee not found with ID: {}", id);
            throw new NoSuchElementException("Trainee not found with ID: " + id);
        }
        logger.debug("Fetched trainee with ID: {}", id);
        return trainee;
    }

    @Override
    public List<Trainee> getAllTrainee() {
        logger.debug("Fetching all trainees");
        return traineeDao.getAll();
    }
}

package org.example.facade.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.facade.GymFacadeService;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;


@Service
public class GymFacadeServiceImpl implements GymFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(GymFacadeServiceImpl.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;


    //Changed to manual constructor instead of Lombok
    public GymFacadeServiceImpl(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // Create

    @Override
    public Trainee registerTrainee(String firstName, String lastName, String dob, String address) {
        logger.info("Facade: Registering trainee {}", firstName + " " + lastName);
        return traineeService.createTrainee(firstName, lastName, dob, address);
    }

    @Override
    public Trainer registerTrainer(String firstName, String lastName, String specialization) {
        logger.info("Facade: Registering trainer {}", firstName + " " + lastName);
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    @Override
    public Training scheduleTraining(Long traineeId, Long trainerId, String name, String type, String date, Duration duration) {
        logger.info("Facade: Scheduling training: {}", name);
        return trainingService.createTraining(traineeId, trainerId, name, type, date, duration);
    }

    // Read

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeService.getAllTrainee();
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainer();
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    // Update

    @Override
    public void updateTrainee(Trainee trainee) {
        logger.info("Facade: Updating trainee ID={}", trainee.getId());
        traineeService.updateTrainee(trainee);
    }

    @Override
    public void updateTrainer(Trainer trainer) {
        logger.info("Facade: Updating trainer ID={}", trainer.getId());
        trainerService.updateTrainer(trainer);
    }

    @Override
    public void updateTraining(Training training) {
        logger.info("Facade: Updating training ID={}", training.getId());
        trainingService.updateTraining(training);
    }

    // Delete

    @Override
    public void deleteTrainee(Long id) {
        logger.info("Facade: Deleting trainee ID={}", id);
        traineeService.deleteTrainee(id);
    }

    @Override
    public void deleteTrainer(Long id) {
        logger.info("Facade: Deleting trainer ID={}", id);
        trainerService.deleteTrainer(id);
    }

    @Override
    public void deleteTraining(Long id) {
        logger.info("Facade: Deleting training ID={}", id);
        trainingService.deleteTraining(id);
    }
}

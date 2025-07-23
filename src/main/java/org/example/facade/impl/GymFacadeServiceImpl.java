package org.example.facade.impl;

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

import java.util.List;

@Service
public class GymFacadeServiceImpl implements GymFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(GymFacadeServiceImpl.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;


    public GymFacadeServiceImpl(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        logger.info("GymFacadeService initialized with core services.");
    }

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
    public Training scheduleTraining(Long traineeId, Long trainerId, String name, String type, String date, String duration) {
        logger.info("Facade: Scheduling training: {}", name);
        return trainingService.createTraining(traineeId, trainerId, name, type, date, duration);
    }

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
}

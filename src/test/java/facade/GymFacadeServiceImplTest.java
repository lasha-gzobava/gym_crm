
package facade;

import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.facade.impl.GymFacadeServiceImpl;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeServiceImplTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;
    private GymFacadeServiceImpl facade;

    @BeforeEach
    void setUp() {
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        trainingService = mock(TrainingService.class);
        facade = new GymFacadeServiceImpl(traineeService, trainerService, trainingService);
    }

    @Test
    void testRegisterTrainee() {
        Trainee mockTrainee = new Trainee(1L, "2000-01-01", "address", 100L);
        when(traineeService.createTrainee("John", "Doe", "2000-01-01", "address")).thenReturn(mockTrainee);
        Trainee result = facade.registerTrainee("John", "Doe", "2000-01-01", "address");
        assertEquals(mockTrainee, result);
        verify(traineeService).createTrainee("John", "Doe", "2000-01-01", "address");
    }

    @Test
    void testRegisterTrainer() {
        Trainer mockTrainer = new Trainer(1L, "Cardio", 200L);
        when(trainerService.createTrainer("Jane", "Smith", "Cardio")).thenReturn(mockTrainer);
        Trainer result = facade.registerTrainer("Jane", "Smith", "Cardio");
        assertEquals(mockTrainer, result);
        verify(trainerService).createTrainer("Jane", "Smith", "Cardio");
    }

    @Test
    void testScheduleTraining() {
        Training mockTraining = new Training(1L, 1L, 2L, "Yoga", "Fitness", "2024-01-01", Duration.ofHours(1));
        when(trainingService.createTraining(1L, 2L, "Yoga", "Fitness", "2024-01-01", Duration.ofHours(1))).thenReturn(mockTraining);
        Training result = facade.scheduleTraining(1L, 2L, "Yoga", "Fitness", "2024-01-01", Duration.ofHours(1));
        assertEquals(mockTraining, result);
        verify(trainingService).createTraining(1L, 2L, "Yoga", "Fitness", "2024-01-01", Duration.ofHours(1));
    }

    @Test
    void testGetAllEntities() {
        facade.getAllTrainees();
        verify(traineeService).getAllTrainee();
        facade.getAllTrainers();
        verify(trainerService).getAllTrainer();
        facade.getAllTrainings();
        verify(trainingService).getAllTrainings();
    }

    @Test
    void testUpdateAndDelete() {
        Trainee trainee = new Trainee(1L, "dob", "addr", 100L);
        Trainer trainer = new Trainer(2L, "spec", 200L);
        Training training = new Training(3L, 1L, 2L, "name", "type", "date", Duration.ofHours(1));

        facade.updateTrainee(trainee);
        verify(traineeService).updateTrainee(trainee);
        facade.updateTrainer(trainer);
        verify(trainerService).updateTrainer(trainer);
        facade.updateTraining(training);
        verify(trainingService).updateTraining(training);

        facade.deleteTrainee(1L);
        verify(traineeService).deleteTrainee(1L);
        facade.deleteTrainer(2L);
        verify(trainerService).deleteTrainer(2L);
        facade.deleteTraining(3L);
        verify(trainingService).deleteTraining(3L);
    }
}

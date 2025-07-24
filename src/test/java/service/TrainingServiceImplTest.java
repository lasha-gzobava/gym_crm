package service;

import org.example.dao.TrainingDao;
import org.example.entity.Training;
import org.example.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    private TrainingServiceImpl trainingService;
    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl();
        trainingDao = mock(TrainingDao.class);
        trainingService.setTrainingDao(trainingDao);
    }

    @Test
    void testCreateTraining() {
        Training training = trainingService.createTraining(1L, 2L, "Cardio",
                "Endurance", "2024-01-01", Duration.ofMinutes(60));

        assertNotNull(training);
        assertEquals("Cardio", training.getTrainingName());
        assertEquals("Endurance", training.getTrainingType());
        verify(trainingDao).save(any(Training.class));
    }

    @Test
    void testUpdateTrainingSuccess() {
        Training training = new Training(1L, 1L, 2L, "Yoga", "Flexibility", "2024-01-01", Duration.ofMinutes(45));
        when(trainingDao.update(training)).thenReturn(Optional.of(training));

        assertDoesNotThrow(() -> trainingService.updateTraining(training));
        verify(trainingDao).update(training);
    }

    @Test
    void testUpdateTrainingFailure() {
        Training training = new Training(99L, 1L, 2L, "Boxing", "Strength", "2024-01-01", Duration.ofMinutes(30));
        when(trainingDao.update(training)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.updateTraining(training));
    }

    @Test
    void testDeleteTrainingSuccess() {
        Training training = new Training(1L, 1L, 2L, "Zumba", "Dance", "2024-01-01", Duration.ofMinutes(50));
        when(trainingDao.getById(1L)).thenReturn(training);

        assertDoesNotThrow(() -> trainingService.deleteTraining(1L));
        verify(trainingDao).delete(1L);
    }

    @Test
    void testDeleteTrainingFailure() {
        when(trainingDao.getById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> trainingService.deleteTraining(99L));
    }

    @Test
    void testGetTrainingByIdSuccess() {
        Training training = new Training(1L, 1L, 2L, "Spin", "Cycling", "2024-01-01", Duration.ofMinutes(40));
        when(trainingDao.getById(1L)).thenReturn(training);

        Training result = trainingService.getTrainingById(1L);
        assertEquals("Spin", result.getTrainingName());
    }

    @Test
    void testGetTrainingByIdFailure() {
        when(trainingDao.getById(42L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> trainingService.getTrainingById(42L));
    }

    @Test
    void testGetAllTrainings() {
        List<Training> trainings = Arrays.asList(
                new Training(1L, 1L, 2L, "Yoga", "Flexibility", "2024-01-01", Duration.ofMinutes(45)),
                new Training(2L, 2L, 3L, "HIIT", "Endurance", "2024-02-01", Duration.ofMinutes(30))
        );

        when(trainingDao.getAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAllTrainings();
        assertEquals(2, result.size());
    }
}

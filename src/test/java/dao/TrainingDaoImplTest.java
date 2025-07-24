
package dao;

import org.example.dao.impl.TrainingDaoImpl;
import org.example.entity.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingDaoImplTest {

    private TrainingDaoImpl trainingDao;
    private Map<Long, Training> trainingStorage;

    @BeforeEach
    public void setup() {
        trainingDao = new TrainingDaoImpl();
        trainingStorage = new HashMap<>();
        trainingDao.setTrainingStorage(trainingStorage);
    }

    @Test
    public void testSaveAndGetById() {
        Training training = new Training(1L, 100L, 200L, "Cardio", "Fitness", "2025-07-24", Duration.ofMinutes(60));
        trainingDao.save(training);

        Training retrieved = trainingDao.getById(1L);
        assertNotNull(retrieved);
        assertEquals("Cardio", retrieved.getTrainingName());
    }

    @Test
    public void testSaveDuplicateThrowsException() {
        Training training = new Training(1L, 100L, 200L, "Cardio", "Fitness", "2025-07-24", Duration.ofMinutes(60));
        trainingDao.save(training);

        assertThrows(IllegalArgumentException.class, () -> trainingDao.save(training));
    }

    @Test
    public void testUpdateExisting() {
        Training training = new Training(1L, 100L, 200L, "Yoga", "Wellness", "2025-07-24", Duration.ofMinutes(45));
        trainingDao.save(training);

        training.setTrainingName("Advanced Yoga");
        Optional<Training> updated = trainingDao.update(training);

        assertTrue(updated.isPresent());
        assertEquals("Advanced Yoga", updated.get().getTrainingName());
    }

    @Test
    public void testUpdateNonExistingReturnsEmpty() {
        Training training = new Training(1L, 100L, 200L, "Pilates", "Fitness", "2025-07-24", Duration.ofMinutes(30));
        Optional<Training> result = trainingDao.update(training);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDelete() {
        Training training = new Training(1L, 100L, 200L, "Spin", "Cardio", "2025-07-24", Duration.ofMinutes(50));
        trainingDao.save(training);

        trainingDao.delete(1L);
        assertNull(trainingDao.getById(1L));
    }

    @Test
    public void testGetAll() {
        trainingDao.save(new Training(1L, 100L, 200L, "HIIT", "Cardio", "2025-07-24", Duration.ofMinutes(20)));
        trainingDao.save(new Training(2L, 101L, 201L, "Stretch", "Wellness", "2025-07-25", Duration.ofMinutes(30)));

        List<Training> all = trainingDao.getAll();
        assertEquals(2, all.size());
    }
}

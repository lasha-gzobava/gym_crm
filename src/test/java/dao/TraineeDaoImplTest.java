package dao;

import org.example.dao.TraineeDao;
import org.example.dao.impl.TraineeDaoImpl;
import org.example.entity.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDaoImplTest {

    private TraineeDaoImpl traineeDao;
    private Map<Long, Trainee> storage;

    @BeforeEach
    void setUp() {
        storage = new HashMap<>();
        traineeDao = new TraineeDaoImpl();
        traineeDao.setTraineeStorage(storage);
    }

    @Test
    void save_shouldAddNewTrainee() {
        Trainee trainee = new Trainee(1L, "2000-01-01", "Address", 10L);
        Trainee saved = traineeDao.save(trainee);
        assertEquals(trainee, saved);
        assertEquals(trainee, storage.get(1L));
    }

    @Test
    void save_shouldThrowExceptionWhenTraineeExists() {
        Trainee trainee = new Trainee(1L, "2000-01-01", "Address", 10L);
        traineeDao.save(trainee);
        assertThrows(IllegalArgumentException.class, () -> traineeDao.save(trainee));
    }

    @Test
    void update_shouldUpdateExistingTrainee() {
        Trainee trainee = new Trainee(1L, "2000-01-01", "Address", 10L);
        storage.put(1L, trainee);
        trainee.setAddress("New Address");
        Optional<Trainee> updated = traineeDao.update(trainee);
        assertTrue(updated.isPresent());
        assertEquals("New Address", updated.get().getAddress());
    }

    @Test
    void update_shouldReturnEmptyWhenTraineeNotFound() {
        Trainee trainee = new Trainee(1L, "2000-01-01", "Address", 10L);
        Optional<Trainee> updated = traineeDao.update(trainee);
        assertTrue(updated.isEmpty());
    }

    @Test
    void delete_shouldRemoveTrainee() {
        Trainee trainee = new Trainee(1L, "2000-01-01", "Address", 10L);
        storage.put(1L, trainee);
        traineeDao.delete(1L);
        assertFalse(storage.containsKey(1L));
    }

    @Test
    void getById_shouldReturnTraineeWhenFound() {
        Trainee trainee = new Trainee(1L, "2000-01-01", "Address", 10L);
        storage.put(1L, trainee);
        Trainee found = traineeDao.getById(1L);
        assertNotNull(found);
        assertEquals(trainee, found);
    }

    @Test
    void getById_shouldReturnNullWhenNotFound() {
        Trainee found = traineeDao.getById(2L);
        assertNull(found);
    }

    @Test
    void getAll_shouldReturnAllTrainees() {
        Trainee t1 = new Trainee(1L, "2000-01-01", "Address1", 10L);
        Trainee t2 = new Trainee(2L, "2001-01-01", "Address2", 11L);
        storage.put(1L, t1);
        storage.put(2L, t2);
        List<Trainee> all = traineeDao.getAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }
}

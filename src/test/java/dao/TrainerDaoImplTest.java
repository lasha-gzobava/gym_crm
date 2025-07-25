package dao;

import org.example.dao.impl.TrainerDaoImpl;
import org.example.entity.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoImplTest {

    private TrainerDaoImpl trainerDao;

    @BeforeEach
    void setUp() {
        trainerDao = new TrainerDaoImpl();
        Map<Long, Trainer> storage = new HashMap<>();
        trainerDao.setTrainerStorage(storage);
    }

    @Test
    void testSaveAndGetById() {
        Trainer trainer = new Trainer(1L, "Strength", 10L);
        trainerDao.save(trainer);

        Trainer fetched = trainerDao.getById(1L);
        assertNotNull(fetched);
        assertEquals("Strength", fetched.getSpecialization());
    }

    @Test
    void testSaveDuplicateThrowsException() {
        Trainer trainer = new Trainer(1L, "Strength", 10L);
        trainerDao.save(trainer);

        assertThrows(IllegalArgumentException.class, () -> trainerDao.save(trainer));
    }

    @Test
    void testUpdateExistingTrainer() {
        Trainer trainer = new Trainer(1L, "Strength", 10L);
        trainerDao.save(trainer);

        trainer.setSpecialization("Cardio");
        Optional<Trainer> updated = trainerDao.update(trainer);

        assertTrue(updated.isPresent());
        assertEquals("Cardio", updated.get().getSpecialization());
    }

    @Test
    void testUpdateNonExistingTrainer() {
        Trainer trainer = new Trainer(1L, "Yoga", 10L);
        Optional<Trainer> result = trainerDao.update(trainer);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteTrainer() {
        Trainer trainer = new Trainer(1L, "Pilates", 10L);
        trainerDao.save(trainer);

        trainerDao.delete(1L);
        assertNull(trainerDao.getById(1L));
    }

    @Test
    void testGetAll() {
        trainerDao.save(new Trainer(1L, "Strength", 10L));
        trainerDao.save(new Trainer(2L, "Yoga", 11L));

        List<Trainer> all = trainerDao.getAll();
        assertEquals(2, all.size());
    }
}
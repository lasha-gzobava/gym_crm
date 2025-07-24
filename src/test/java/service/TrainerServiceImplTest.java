package service;

import org.example.dao.TrainerDao;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    private TrainerServiceImpl trainerService;
    private TrainerDao trainerDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl();

        trainerDao = mock(TrainerDao.class);
        userService = mock(UserService.class);

        trainerService.setTrainerDao(trainerDao);
        trainerService.setUserService(userService);
    }

    @Test
    void testCreateTrainer() {
        User user = new User(1L, "John", "Doe", "johndoe", "secret", true);
        when(userService.createUser("John", "Doe")).thenReturn(user);

        Trainer created = trainerService.createTrainer("John", "Doe", "Yoga");

        assertNotNull(created);
        assertEquals("Yoga", created.getSpecialization());
        assertEquals(user.getId(), created.getUserId());

        verify(trainerDao).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainerSuccess() {
        Trainer trainer = new Trainer(1L, "Pilates", 2L);
        when(trainerDao.update(trainer)).thenReturn(Optional.of(trainer));

        assertDoesNotThrow(() -> trainerService.updateTrainer(trainer));
        verify(trainerDao).update(trainer);
    }

    @Test
    void testUpdateTrainerNotFound() {
        Trainer trainer = new Trainer(99L, "Boxing", 3L);
        when(trainerDao.update(trainer)).thenReturn(Optional.empty());

        NoSuchElementException e = assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTrainer(trainer));
        assertTrue(e.getMessage().contains("Trainer not found"));
    }

    @Test
    void testDeleteTrainerSuccess() {
        Trainer trainer = new Trainer(1L, "Crossfit", 2L);
        when(trainerDao.getById(1L)).thenReturn(trainer);

        assertDoesNotThrow(() -> trainerService.deleteTrainer(1L));
        verify(trainerDao).delete(1L);
    }

    @Test
    void testDeleteTrainerNotFound() {
        when(trainerDao.getById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> trainerService.deleteTrainer(99L));
    }

    @Test
    void testGetTrainerByIdSuccess() {
        Trainer trainer = new Trainer(1L, "Strength", 10L);
        when(trainerDao.getById(1L)).thenReturn(trainer);

        Trainer result = trainerService.getTrainerById(1L);

        assertEquals("Strength", result.getSpecialization());
    }

    @Test
    void testGetTrainerByIdNotFound() {
        when(trainerDao.getById(42L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> trainerService.getTrainerById(42L));
    }

    @Test
    void testGetAllTrainer() {
        List<Trainer> mockList = Arrays.asList(
                new Trainer(1L, "Cardio", 100L),
                new Trainer(2L, "Zumba", 101L)
        );
        when(trainerDao.getAll()).thenReturn(mockList);

        List<Trainer> result = trainerService.getAllTrainer();

        assertEquals(2, result.size());
        assertEquals("Zumba", result.get(1).getSpecialization());
    }
}

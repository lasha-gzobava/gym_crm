
package service;

import org.example.dao.TraineeDao;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.example.service.UserService;
import org.example.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    private TraineeServiceImpl traineeService;
    private TraineeDao traineeDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl();
        traineeDao = mock(TraineeDao.class);
        userService = mock(UserService.class);
        traineeService.setTraineeDao(traineeDao);
        traineeService.setUserService(userService);
    }

    @Test
    void testCreateTrainee() {
        User mockUser = new User(1L, "John", "Doe", "John.Doe", "encoded", true);
        when(userService.createUser("John", "Doe")).thenReturn(mockUser);

        Trainee result = traineeService.createTrainee("John", "Doe", "1990-01-01", "123 Street");

        assertNotNull(result);
        assertEquals("1990-01-01", result.getDateOfBirth());
        assertEquals("123 Street", result.getAddress());
        assertEquals(mockUser.getId(), result.getUserId());

        verify(traineeDao, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTraineeSuccess() {
        Trainee trainee = new Trainee(1L, "1990-01-01", "Address", 1L);
        when(traineeDao.update(trainee)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.updateTrainee(trainee));
    }

    @Test
    void testUpdateTraineeFail() {
        Trainee trainee = new Trainee(99L, "1990-01-01", "Nowhere", 1L);
        when(traineeDao.update(trainee)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.updateTrainee(trainee));
    }

    @Test
    void testDeleteTraineeSuccess() {
        Trainee trainee = new Trainee(1L, "1990-01-01", "Address", 1L);
        when(traineeDao.getById(1L)).thenReturn(trainee);

        assertDoesNotThrow(() -> traineeService.deleteTrainee(1L));
        verify(traineeDao).delete(1L);
    }

    @Test
    void testDeleteTraineeFail() {
        when(traineeDao.getById(42L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> traineeService.deleteTrainee(42L));
    }

    @Test
    void testGetTraineeByIdSuccess() {
        Trainee trainee = new Trainee(1L, "1990-01-01", "Address", 1L);
        when(traineeDao.getById(1L)).thenReturn(trainee);

        Trainee found = traineeService.getTraineeById(1L);
        assertEquals(trainee, found);
    }

    @Test
    void testGetTraineeByIdFail() {
        when(traineeDao.getById(42L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeById(42L));
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = List.of(
                new Trainee(1L, "1990-01-01", "Addr1", 1L),
                new Trainee(2L, "1992-02-02", "Addr2", 2L)
        );
        when(traineeDao.getAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAllTrainee();
        assertEquals(2, result.size());
    }
}

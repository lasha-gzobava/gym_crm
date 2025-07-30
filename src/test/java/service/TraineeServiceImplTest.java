package service;

import org.example.dto.CreateTraineeDto;
import org.example.dto.CreateUserDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.TraineeDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.User;
import org.example.mapper.TraineeMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.service.UserService;
import org.example.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private TraineeMapper traineeMapper;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingRepository trainingRepository;
    @Mock private UserService userService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_shouldCreateAndReturnDto() {
        CreateUserDto userDto = new CreateUserDto("John", "Doe");
        CreateTraineeDto dto = new CreateTraineeDto();
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAddress("Address");
        dto.setUser(userDto);

        User user = new User("John", "Doe", "jdoe", "secret123");
        Trainee trainee = new Trainee(dto.getDateOfBirth(), dto.getAddress(), user);
        TraineeDto expected = new TraineeDto();

        when(userService.createUser("John", "Doe")).thenReturn(user);
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeDto result = traineeService.createTrainee(dto);
        assertEquals(expected, result);
    }

    @Test
    void getByUsername_shouldReturnDtoWhenAuthenticated() {
        String username = "jdoe";
        String password = "secret123";
        Trainee trainee = new Trainee();
        TraineeDto dto = new TraineeDto();

        User user = new User("John", "Doe", "jdoe", "secret123");

        when(userService.authenticate(username, password)).thenReturn(user);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDto(trainee)).thenReturn(dto);

        TraineeDto result = traineeService.getByUsername(username, password);
        assertEquals(dto, result);
    }

    @Test
    void changePassword_shouldDelegateToUserService() {
        PasswordChangeDto dto = new PasswordChangeDto("jdoe", "oldPass", "newPass");
        traineeService.changePassword(dto);
        verify(userService).changePassword("jdoe", "oldPass", "newPass");
    }

    @Test
    void updateTrainee_shouldUpdateFieldsCorrectly() {
        String username = "jdoe";
        String password = "secret123";

        CreateUserDto userDto = new CreateUserDto("Updated", "Name");
        CreateTraineeDto dto = new CreateTraineeDto();
        dto.setDateOfBirth(LocalDate.of(2000, 2, 2));
        dto.setAddress("New Address");
        dto.setUser(userDto);



        User user = new User("John", "Doe", "jdoe", "secret123");
        Trainee trainee = new Trainee(LocalDate.of(1990, 1, 1), "Old Address", user);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.updateTrainee(username, dto, password);

        assertEquals("Updated", trainee.getUser().getFirstName());
        assertEquals("Name", trainee.getUser().getLastName());
        assertEquals("New Address", trainee.getAddress());
        assertEquals(LocalDate.of(2000, 2, 2), trainee.getDateOfBirth());

        verify(traineeRepository).save(trainee);
    }

    @Test
    void toggleActive_shouldAuthenticateAndToggle() {
        String username = "jdoe";
        String password = "secret123";

        traineeService.toggleActive(username, password);

        verify(userService).authenticate(username, password);
        verify(userService).toggleActive(username);
    }

    @Test
    void deleteByUsername_shouldRemoveTraineeAndTrainings() {
        String username = "jdoe";
        String password = "secret123";
        User user = new User("John", "Doe", "jdoe", "secret123");
        Trainee trainee = new Trainee(LocalDate.of(1990, 1, 1), "Address", user);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.deleteByUsername(username, password);

        verify(trainingRepository).deleteAllByTrainee(trainee);
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void updateAssignedTrainers_shouldSetNewTrainers() {
        String username = "jdoe";
        List<Long> ids = List.of(1L, 2L);
        User user = new User("John", "Doe", "jdoe", "secret123");
        Trainee trainee = new Trainee(LocalDate.of(1990, 1, 1), "Address", user);
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllById(ids)).thenReturn(trainers);

        traineeService.updateAssignedTrainers(username, ids);

        assertEquals(trainers, trainee.getTrainers());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainersList_shouldUpdateIfAllValid() {
        String username = "jdoe";
        List<Long> ids = List.of(1L, 2L);
        User user = new User("John", "Doe", "jdoe", "secret123");
        Trainee trainee = new Trainee(LocalDate.of(1990, 1, 1), "Address", user);
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllById(ids)).thenReturn(trainers);

        traineeService.updateTrainersList(username, ids);

        assertEquals(trainers, trainee.getTrainers());
        verify(traineeRepository).save(trainee);
    }
}

package service;

import org.example.dto.CreateTrainerDto;
import org.example.dto.CreateUserDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.TrainerDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.mapper.TrainerMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.UserService;
import org.example.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainerMapper trainerMapper;
    @Mock private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_shouldCreateAndReturnDto() {
        CreateUserDto userDto = new CreateUserDto("Alice", "Smith");
        CreateTrainerDto dto = new CreateTrainerDto();
        dto.setUser(userDto);
        dto.setSpecialization("Yoga");

        TrainingType trainingType = new TrainingType();
        User user = new User("Alice", "Smith", "asmith", "secret");
        Trainer trainer = new Trainer(trainingType, user);
        TrainerDto expected = new TrainerDto();

        when(trainingTypeRepository.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(trainingType));
        when(userService.createUser("Alice", "Smith")).thenReturn(user);
        when(trainerRepository.save(any())).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expected);

        TrainerDto result = trainerService.createTrainer(dto);
        assertEquals(expected, result);
    }

    @Test
    void getByUsername_shouldAuthenticateAndReturnDto() {
        String username = "asmith";
        String password = "secret";
        User user = new User("Alice", "Smith", username, password);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        TrainerDto dto = new TrainerDto();

        when(userService.authenticate(username, password)).thenReturn(user);
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(dto);

        TrainerDto result = trainerService.getByUsername(username, password);
        assertEquals(dto, result);
    }

    @Test
    void changePassword_shouldDelegateToUserService() {
        PasswordChangeDto dto = new PasswordChangeDto("asmith", "oldPass", "newPass");
        trainerService.changePassword(dto);
        verify(userService).changePassword("asmith", "oldPass", "newPass");
    }

    @Test
    void updateTrainer_shouldUpdateTrainerAndSave() {
        String username = "asmith";
        String password = "secret";

        CreateUserDto userDto = new CreateUserDto("Updated", "Trainer");
        CreateTrainerDto dto = new CreateTrainerDto();
        dto.setUser(userDto);
        dto.setSpecialization("Pilates");

        TrainingType trainingType = new TrainingType();
        User user = new User("Alice", "Smith", username, password);
        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(userService.authenticate(username, password)).thenReturn(user);
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Pilates")).thenReturn(Optional.of(trainingType));

        trainerService.updateTrainer(username, dto, password);

        assertEquals("Updated", trainer.getUser().getFirstName());
        assertEquals("Trainer", trainer.getUser().getLastName());
        assertEquals(trainingType, trainer.getSpecialization());
        verify(trainerRepository).save(trainer);
    }

    @Test
    void toggleActive_shouldAuthenticateAndToggle() {
        String username = "asmith";
        String password = "secret";

        trainerService.toggleActive(username, password);

        verify(userService).authenticate(username, password);
        verify(userService).toggleActive(username);
    }

    @Test
    void deleteByUsername_shouldAuthenticateAndDeleteTrainer() {
        String username = "asmith";
        String password = "secret";
        Trainer trainer = new Trainer();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deleteByUsername(username, password);

        verify(trainerRepository).delete(trainer);
    }

    @Test
    void getUnassignedTrainersForTrainee_shouldReturnOnlyUnassigned() {
        String traineeUsername = "john_doe";

        Trainer trainer1 = new Trainer(); trainer1.setTrainerId(1L);
        Trainer trainer2 = new Trainer(); trainer2.setTrainerId(2L);
        Trainer trainer3 = new Trainer(); trainer3.setTrainerId(3L);

        Trainer assignedTrainer = new Trainer(); assignedTrainer.setTrainerId(1L);

        Trainee trainee = new Trainee();
        trainee.setTrainers(List.of(assignedTrainer));

        List<Trainer> allTrainers = List.of(trainer1, trainer2, trainer3);

        TrainerDto dto2 = new TrainerDto();
        TrainerDto dto3 = new TrainerDto();

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(allTrainers);
        when(trainerMapper.toDto(trainer2)).thenReturn(dto2);
        when(trainerMapper.toDto(trainer3)).thenReturn(dto3);

        List<TrainerDto> result = trainerService.getUnassignedTrainersForTrainee(traineeUsername);

        assertEquals(2, result.size());
        assertTrue(result.contains(dto2));
        assertTrue(result.contains(dto3));
    }

}

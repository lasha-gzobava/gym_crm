package service;

import org.example.dto.trainer.CreateTrainerDto;
import org.example.dto.user.CreateUserDto;
import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainer.TrainerDto;
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

import java.util.*;

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
        CreateTrainerDto dto = new CreateTrainerDto();
        CreateUserDto userDto = new CreateUserDto("Alice", "Smith");
        dto.setUser(userDto);
        dto.setSpecialization("Yoga");

        TrainingType specialization = new TrainingType("Yoga");
        when(trainingTypeRepository.findByTrainingTypeName("Yoga"))
                .thenReturn(Optional.of(specialization));

        User user = new User("Alice", "Smith", "asmith", "encodedPass");
        when(userService.createUser("Alice", "Smith")).thenReturn(user);

        Trainer trainer = new Trainer(specialization, user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        TrainerDto expectedDto = new TrainerDto();  // minimal dummy
        when(trainerMapper.toDto(any(Trainer.class))).thenReturn(expectedDto);

        TrainerDto result = trainerService.createTrainer(dto);

        assertEquals(expectedDto, result);
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
        String traineeUsername = "jdoe";

        User user = new User("John", "Doe", "jdoe", "pass");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Trainer assignedTrainer = new Trainer();
        Trainer unassignedTrainer = new Trainer();

        List<Trainer> allTrainers = List.of(assignedTrainer, unassignedTrainer);
        trainee.setTrainers(List.of(assignedTrainer)); // must be same instance

        when(traineeRepository.findWithTrainersByUserUsername(traineeUsername))
                .thenReturn(Optional.of(trainee));
        when(trainerRepository.findAll()).thenReturn(allTrainers);
        when(trainerMapper.toDto(unassignedTrainer)).thenReturn(new TrainerDto());

        List<TrainerDto> result = trainerService.getUnassignedTrainersForTrainee(traineeUsername);

        assertEquals(1, result.size());
    }



}

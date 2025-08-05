package service;

import org.example.dto.trainee.*;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.training.TraineeTrainingRequestDto;
import org.example.dto.training.TraineeTrainingResponseDto;
import org.example.dto.user.UserCreateDto;
import org.example.entity.*;
import org.example.repository.*;
import org.example.service.UserService;
import org.example.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterWithCredentials_ShouldReturnCredentials() {
        // Arrange
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName("John");
        userCreateDto.setLastName("Doe");

        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setUser(userCreateDto);
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setAddress("123 Main St");

        User user = new User("John", "Doe", "john.doe", "hashed");

        when(userService.createUser("John", "Doe")).thenReturn(user);
        when(userService.getRawPassword()).thenReturn("rawPassword");

        // Act
        TraineeCredentialsDto credentials = traineeService.registerWithCredentials(dto);

        // Assert
        assertNotNull(credentials);
        assertEquals("john.doe", credentials.getUsername());
        assertEquals("rawPassword", credentials.getPassword());
    }

    @Test
    void testGetTraineeProfile_ShouldReturnProfile() {
        String username = "john.doe";
        User user = new User("John", "Doe", username, "hashed");
        user.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setAddress("123 St");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setTrainers(List.of()); // Empty list for simplicity

        when(userService.authenticate(eq(username), anyString())).thenReturn(user);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        TraineeProfileDto result = traineeService.getTraineeProfile(username, "pass");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertEquals("123 St", result.getAddress());
        assertTrue(result.getIsActive());
        assertNotNull(result.getTrainers());
        assertTrue(result.getTrainers().isEmpty());
    }




    @Test
    void testGetTraineeProfile_NotFound_ShouldThrow() {
        String username = "not.found";
        when(userService.authenticate(eq(username), anyString())).thenReturn(new User());
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> traineeService.getTraineeProfile(username, "pass"));
    }


    @Test
    void testDeleteByUsername_ShouldDelete() {
        String username = "john.doe";
        User user = new User();
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(userService.authenticate(eq(username), anyString())).thenReturn(user);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.deleteByUsername(username, "pass"));
        verify(traineeRepository).delete(trainee);
    }


    @Test
    void testDeleteByUsername_TraineeNotFound_ShouldThrow() {
        String username = "not.found";
        when(userService.authenticate(eq(username), anyString())).thenReturn(new User());
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> traineeService.deleteByUsername(username, "pass"));
    }

    @Test
    void testToggleActive_ShouldUpdateIsActive() {
        String username = "john.doe";
        Trainee trainee = new Trainee();
        trainee.setUser(new User());

        when(userService.authenticate(eq(username), anyString())).thenReturn(new User());
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        assertDoesNotThrow(() -> traineeService.toggleActive(username, true, "pass"));
        verify(traineeRepository, never()).delete(any());
    }


    @Test
    void testUpdateProfile_ShouldUpdateSuccessfully() {
        String username = "john.doe";
        TraineeProfileUpdateDto dto = new TraineeProfileUpdateDto();
        dto.setUsername(username);
        dto.setAddress("New Address");
        dto.setDateOfBirth(LocalDate.of(1995, 1, 1));

        User user = new User();
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(userService.authenticate(eq(username), anyString())).thenReturn(user);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        TraineeProfileDto result = traineeService.updateProfile(dto, "pass");

        assertNotNull(result);
        assertEquals("New Address", result.getAddress());
    }


    @Test
    void testUpdateTraineeTrainers_ShouldUpdateSuccessfully() {
        // Given
        String traineeUsername = "john.doe";

        TraineeTrainerUpdateDto dto = new TraineeTrainerUpdateDto();
        dto.setTraineeUsername(traineeUsername);
        dto.setTrainerUsernames(List.of("trainer1"));

        User user = new User("John", "Doe", traineeUsername, "hashed");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeId(1L);
        trainingType.setTrainingTypeName("Yoga");

        Trainer trainer = new Trainer();
        trainer.setUser(new User("Trainer", "One", "trainer1", "hashed"));
        trainer.setSpecialization(trainingType); // 🔥 FIX HERE

        // Mocking
        when(userService.authenticate(eq(traineeUsername), anyString())).thenReturn(user);
        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllByUser_UsernameIn(anyList())).thenReturn(List.of(trainer));

        // When
        List<TrainerForTrainerListDto> result = traineeService.updateTraineeTrainers(dto, "pass");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Yoga", result.get(0).getSpecialization()); // assuming this field is mapped
    }



    @Test
    void testUpdateTraineeTrainers_InvalidTrainerList_ShouldThrow() {
        TraineeTrainerUpdateDto dto = new TraineeTrainerUpdateDto();
        dto.setTraineeUsername("john.doe");
        dto.setTrainerUsernames(List.of("trainer1", "trainer2"));

        Trainee trainee = new Trainee();
        when(userService.authenticate(eq(dto.getTraineeUsername()), anyString())).thenReturn(new User());
        when(traineeRepository.findByUsername(dto.getTraineeUsername())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllByUser_UsernameIn(anyList())).thenReturn(List.of(new Trainer()));

        assertThrows(RuntimeException.class,
                () -> traineeService.updateTraineeTrainers(dto, "pass"));
    }

    @Test
    void testToggleActive_ShouldUpdateIsActiveToFalse() {
        String username = "john.doe";
        User user = new User();
        user.setUsername(username);
        user.setIsActive(true);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(userService.authenticate(eq(username), anyString())).thenReturn(user);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Act
        traineeService.toggleActive(username, false, "password");

        // Assert
        assertFalse(user.getIsActive()); // confirm value updated
        verify(traineeRepository, never()).delete(any()); // explicitly confirm delete NOT called
    }



    @Test
    void testUpdateProfile_TraineeNotFound_ShouldThrow() {
        String username = "john.doe";
        TraineeProfileUpdateDto dto = new TraineeProfileUpdateDto();
        dto.setUsername(username);

        when(userService.authenticate(eq(username), anyString())).thenReturn(new User());
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.updateProfile(dto, "pass"));
    }

    @Test
    void testGetTraineeTrainingsList_ShouldReturnList() {
        String username = "john.doe";

        // Create request DTO
        TraineeTrainingRequestDto dto = new TraineeTrainingRequestDto();
        dto.setUsername(username);

        // Mock authenticated user and trainee
        User user = new User("John", "Doe", username, "hashed");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Mock trainer and their user
        User trainerUser = new User("Alice", "Smith", "trainer1", "hashed");
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);

        // Mock training type
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Strength");

        // Mock training entity
        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.of(2024, 8, 5));
        training.setTrainingName("Morning Strength");
        training.setTrainingDuration(60L);

        // Mocks
        when(userService.authenticate(eq(username), anyString())).thenReturn(user);
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllByTrainee(trainee)).thenReturn(List.of(training));

        // Execute
        List<TraineeTrainingResponseDto> result = traineeService.getTraineeTrainingsList(dto, "pass");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        TraineeTrainingResponseDto trainingDto = result.get(0);
        assertEquals("Morning Strength", trainingDto.getTrainingName());
        assertEquals(LocalDate.of(2024, 8, 5), trainingDto.getTrainingDate());
        assertEquals("Strength", trainingDto.getTrainingType());
        assertEquals(60L, trainingDto.getTrainingDuration());
        assertEquals("Alice Smith", trainingDto.getTrainerName()); // Assuming name is constructed like that
    }


}

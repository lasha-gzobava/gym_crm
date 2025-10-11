package service;

import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.*;
import org.example.dto.training.TrainerTrainingRequestDto;
import org.example.dto.training.TrainerTrainingResponseDto;
import org.example.entity.*;
import org.example.repository.*;
import org.example.service.UserService;
import org.example.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TrainerServiceImplTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock private TrainerRepository trainerRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private UserService userService;
    @Mock private UserRepository userRepository;
    @Mock private TrainingRepository trainingRepository;

    private User user;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("Jane", "Smith", "jane.smith", "encodedpass");
        user.setIsActive(true);

        trainingType = new TrainingType(1L, "Cardio");

        trainer = new Trainer(trainingType, user);
    }

    @Test
    void registerWithCredentials_shouldRegisterTrainerSuccessfully() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setSpecialization("Cardio");

        when(userService.createUser(any(), any())).thenReturn(user);
        when(userService.getRawPassword()).thenReturn("plaintext");
        when(trainingTypeRepository.findByTrainingTypeName("Cardio")).thenReturn(Optional.of(trainingType));

        TraineeCredentialsDto result = trainerService.registerWithCredentials(dto);

        assertNotNull(result);
        assertEquals("jane.smith", result.getUsername());
        assertEquals("plaintext", result.getPassword());
    }

    @Test
    void getTrainerProfile_shouldReturnProfile() {
        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(traineeRepository.findAllByTrainers_User_Username("jane.smith")).thenReturn(Collections.emptyList());

        TrainerProfileDto profile = trainerService.getTrainerProfile("jane.smith", "pass");

        assertEquals("jane.smith", profile.getUsername());
        assertEquals("Jane", profile.getFirstName());
        assertEquals("Smith", profile.getLastName());
        assertEquals("Cardio", profile.getSpecialization());
        assertTrue(profile.isActive());
        assertTrue(profile.getTrainees().isEmpty());
    }

    @Test
    void updateTrainerProfile_shouldUpdateSuccessfully() {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane.smith", "Jane", "Smith", "Cardio", true);

        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Cardio")).thenReturn(Optional.of(trainingType));
        when(traineeRepository.findAllByTrainers_User_Username("jane.smith")).thenReturn(Collections.emptyList());

        TrainerProfileDto updated = trainerService.updateTrainerProfile(dto, "pass");

        assertEquals("jane.smith", updated.getUsername());
        assertEquals("Cardio", updated.getSpecialization());
        assertTrue(updated.isActive());
    }

    @Test
    void toggleActive_shouldUpdateActiveStatus_fromFalseToTrue_returnsTrue() {
        String username = "jane.smith";
        String pwd = "pass";
        User user = new User();
        user.setUsername(username);
        user.setIsActive(false);

        // authenticate is called, but we don’t use its return; just let it pass
        when(userService.authenticate(username, pwd)).thenReturn(user);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        boolean changed = trainerService.toggleActive(username, true, pwd);

        assertTrue(changed);
        assertEquals(true, user.getIsActive());
        verify(userService).authenticate(username, pwd);
        verify(userRepository).findByUsername(username);
        verifyNoMoreInteractions(userRepository, userService);
    }


    @Test
    void toggleActive_whenAlreadyActive_returnsFalse_andDoesNotChange() {
        String username = "jane.smith";
        String pwd = "pass";
        User user = new User();
        user.setUsername(username);
        user.setIsActive(true); // already active

        when(userService.authenticate(username, pwd)).thenReturn(user);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        boolean changed = trainerService.toggleActive(username, true, pwd);

        assertFalse(changed);
        assertEquals(true, user.getIsActive()); // unchanged
        verify(userService).authenticate(username, pwd);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void toggleActive_userNotFound_throwsRuntime() {
        String username = "missing.user";
        String pwd = "pass";

        when(userService.authenticate(username, pwd)).thenReturn(new User());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> trainerService.toggleActive(username, true, pwd));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getTrainerTrainingsList_shouldReturnTrainings() {
        TrainerTrainingRequestDto dto = new TrainerTrainingRequestDto("jane.smith", null, null, null);

        Trainee trainee = new Trainee(
                LocalDate.of(1990, 1, 1),
                "Street",
                new User("T1", "L1", "t1", "pwd")
        );

        Training training = new Training(
                trainee,            // correct position
                trainer,            // correct position
                "Training A",       // training name
                trainingType,       // training type
                LocalDate.now(),    // training date
                60L                 // training duration
        );

        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainer(any())).thenReturn(List.of(training));

        List<TrainerTrainingResponseDto> result = trainerService.getTrainerTrainingsList(dto, "pass");

        assertFalse(result.isEmpty());
        assertEquals("Training A", result.get(0).getTrainingName());
        assertEquals("T1 L1", result.get(0).getTraineeName());
    }

    @Test
    void registerWithCredentials_shouldThrow_WhenSpecializationNotFound() {
        TrainerCreateDto dto = new TrainerCreateDto();
        dto.setFirstName("Jane");
        dto.setLastName("Smith");
        dto.setSpecialization("Unknown");

        when(userService.createUser(any(), any())).thenReturn(user);
        when(trainingTypeRepository.findByTrainingTypeName("Unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerService.registerWithCredentials(dto));
    }

    @Test
    void getTrainerProfile_shouldThrow_WhenTrainerNotFound() {
        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerService.getTrainerProfile("jane.smith", "pass"));
    }

    @Test
    void updateTrainerProfile_shouldThrow_WhenTrainerNotFound() {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane.smith", "Jane", "Smith", "Cardio", true);

        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerService.updateTrainerProfile(dto, "pass"));
    }

    @Test
    void updateTrainerProfile_shouldThrow_WhenSpecializationNotFound() {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane.smith", "Jane", "Smith", "Unknown", true);

        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerService.updateTrainerProfile(dto, "pass"));
    }

    @Test
    void getUnassignedTrainersForTrainee_shouldThrow_WhenTraineeNotFound() {
        when(userService.authenticate("trainee1", "pass")).thenReturn(new User());
        when(traineeRepository.findWithTrainersByUserUsername("trainee1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                trainerService.getUnassignedTrainersForTrainee("trainee1", "pass"));
    }

    @Test
    void getTrainerTrainingsList_shouldThrow_WhenTrainerNotFound() {
        TrainerTrainingRequestDto dto = new TrainerTrainingRequestDto("jane.smith", null, null, null);

        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerService.getTrainerTrainingsList(dto, "pass"));
    }

    @Test
    void getTrainerTrainingsList_shouldFilter_ByDateAndTraineeName() {
        TrainerTrainingRequestDto dto = new TrainerTrainingRequestDto(
                "jane.smith",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5),
                "T1 L1"
        );

        User traineeUser = new User("T1", "L1", "t1", "pwd");
        Trainee trainee = new Trainee(LocalDate.of(1990, 1, 1), "Street", traineeUser);

        Training training = new Training(
                trainee,
                trainer,
                "Filtered Training",
                trainingType,
                LocalDate.now(),
                45L
        );

        when(userService.authenticate("jane.smith", "pass")).thenReturn(user);
        when(trainerRepository.findByUsername("jane.smith")).thenReturn(Optional.of(trainer));
        when(trainingRepository.findAllByTrainer(trainer)).thenReturn(List.of(training));

        List<TrainerTrainingResponseDto> result = trainerService.getTrainerTrainingsList(dto, "pass");

        assertEquals(1, result.size());
        assertEquals("Filtered Training", result.get(0).getTrainingName());
    }



}

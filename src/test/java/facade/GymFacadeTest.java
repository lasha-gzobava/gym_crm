package facade;

import jakarta.validation.Validator;
import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainee.CreateTraineeDto;
import org.example.dto.trainee.TraineeDto;
import org.example.dto.trainer.CreateTrainerDto;
import org.example.dto.trainer.TrainerDto;
import org.example.dto.training.CreateTrainingDto;
import org.example.dto.training.TrainingDto;
import org.example.facade.GymFacade;
import org.example.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;
    @Mock private UserService userService;
    @Mock private Validator validator;

    @InjectMocks
    private GymFacade gymFacade;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_shouldDelegateToService() {
        CreateTraineeDto dto = new CreateTraineeDto();
        TraineeDto expected = new TraineeDto();
        when(validator.validate(dto)).thenReturn(Set.of());
        when(traineeService.createTrainee(dto)).thenReturn(expected);

        TraineeDto result = gymFacade.createTrainee(dto);

        assertEquals(expected, result);
        verify(traineeService).createTrainee(dto);
    }

    @Test
    void getTrainee_shouldAuthenticateAndFetch() {
        String username = "john", password = "pass";
        TraineeDto expected = new TraineeDto();
        when(traineeService.getByUsername(username, password)).thenReturn(expected);

        TraineeDto result = gymFacade.getTrainee(username, password);

        assertEquals(expected, result);
        verify(userService).authenticate(username, password);
    }

    @Test
    void createTrainer_shouldDelegate() {
        CreateTrainerDto dto = new CreateTrainerDto();
        TrainerDto expected = new TrainerDto();
        when(validator.validate(dto)).thenReturn(Set.of());
        when(trainerService.createTrainer(dto)).thenReturn(expected);

        TrainerDto result = gymFacade.createTrainer(dto);

        assertEquals(expected, result);
    }

    @Test
    void addTraining_shouldAuthenticateAndDelegate() {
        CreateTrainingDto dto = new CreateTrainingDto();
        TrainingDto expected = new TrainingDto();
        when(validator.validate(dto)).thenReturn(Set.of());
        when(trainingService.addTraining(dto)).thenReturn(expected);

        TrainingDto result = gymFacade.addTraining(dto, "trainer", "pass");

        assertEquals(expected, result);
        verify(userService).authenticate("trainer", "pass");
    }

    @Test
    void getUnassignedTrainers_shouldAuthenticateAndFetch() {
        when(trainerService.getUnassignedTrainersForTrainee("trainee"))
                .thenReturn(Collections.emptyList());

        List<TrainerDto> result = gymFacade.getUnassignedTrainers("trainee", "pass");

        assertTrue(result.isEmpty());
        verify(userService).authenticate("trainee", "pass");
    }

    @Test
    void changeTraineePassword_shouldValidateAndDelegate() {
        PasswordChangeDto dto = new PasswordChangeDto("john", "old", "new");
        when(validator.validate(dto)).thenReturn(Set.of());

        gymFacade.changeTraineePassword(dto);

        verify(userService).authenticate("john", "old");
        verify(traineeService).changePassword(dto);
    }

    @Test
    void getTrainerTrainingsFiltered_shouldCallService() {
        when(trainingService.getTrainingsForTrainer(
                anyString(), anyString(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        List<TrainingDto> result = gymFacade.getTrainerTrainingsFiltered(
                "trainer", "pass", LocalDate.now().minusDays(2), LocalDate.now(), "trainee");

        assertNotNull(result);
    }

    @Test
    void deleteTrainee_shouldCallService() {
        gymFacade.deleteTrainee("john", "pass");

        verify(userService).authenticate("john", "pass");
        verify(traineeService).deleteByUsername("john", "pass");
    }

    @Test
    void toggleTraineeStatus_shouldCallService() {
        gymFacade.toggleTraineeStatus("john", "pass");

        verify(userService).authenticate("john", "pass");
        verify(traineeService).toggleActive("john", "pass");
    }

    @Test
    void changeTrainerPassword_shouldCallService() {
        PasswordChangeDto dto = new PasswordChangeDto("alice", "old", "new");
        when(validator.validate(dto)).thenReturn(Set.of());

        gymFacade.changeTrainerPassword(dto);

        verify(userService).authenticate("alice", "old");
        verify(trainerService).changePassword(dto);
    }

    @Test
    void updateTrainer_shouldCallService() {
        CreateTrainerDto dto = new CreateTrainerDto();
        when(validator.validate(dto)).thenReturn(Set.of());

        gymFacade.updateTrainer("alice", "pass", dto);

        verify(userService).authenticate("alice", "pass");
        verify(trainerService).updateTrainer("alice", dto, "pass");
    }

    @Test
    void toggleTrainerStatus_shouldCallService() {
        gymFacade.toggleTrainerStatus("alice", "pass");

        verify(userService).authenticate("alice", "pass");
        verify(trainerService).toggleActive("alice", "pass");
    }

    @Test
    void getTraineeTrainings_shouldCallService() {
        when(trainingService.getTrainingsForTrainee("john", "pass")).thenReturn(List.of());

        List<TrainingDto> result = gymFacade.getTraineeTrainings("john", "pass");

        assertNotNull(result);
        verify(userService).authenticate("john", "pass");
    }

    @Test
    void getTrainerTrainings_shouldCallService() {
        when(trainingService.getTrainingsForTrainer("alice", "pass")).thenReturn(List.of());

        List<TrainingDto> result = gymFacade.getTrainerTrainings("alice", "pass");

        assertNotNull(result);
        verify(userService).authenticate("alice", "pass");
    }

    @Test
    void updateTraineeTrainers_shouldCallService() {
        List<Long> trainerIds = List.of(1L, 2L);

        gymFacade.updateTraineeTrainers("john", "pass", trainerIds);

        verify(userService).authenticate("john", "pass");
        verify(traineeService).updateTrainersList("john", trainerIds);
    }

}

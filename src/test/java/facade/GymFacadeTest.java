package facade;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.example.dto.*;
import org.example.entity.User;
import org.example.facade.GymFacade;
import org.example.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_shouldValidateAndCallService() {
        CreateTraineeDto dto = new CreateTraineeDto();
        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        TraineeDto expected = new TraineeDto();
        when(traineeService.createTrainee(dto)).thenReturn(expected);

        TraineeDto result = gymFacade.createTrainee(dto);
        assertEquals(expected, result);
    }

    @Test
    void createTrainee_shouldThrowOnValidationFailure() {
        CreateTraineeDto dto = new CreateTraineeDto();
        ConstraintViolation<CreateTraineeDto> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<CreateTraineeDto>> violations = Set.of(violation);
        when(validator.validate(dto)).thenReturn(violations);

        assertThrows(ConstraintViolationException.class, () ->
                gymFacade.createTrainee(dto));
    }

    @Test
    void getTrainee_shouldAuthenticateAndReturnDto() {
        String username = "john", password = "pass";
        TraineeDto expected = new TraineeDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(traineeService.getByUsername(username, password)).thenReturn(expected);

        TraineeDto result = gymFacade.getTrainee(username, password);
        assertEquals(expected, result);
    }

    @Test
    void changeTrainerPassword_shouldValidateAuthenticateAndChange() {
        PasswordChangeDto dto = new PasswordChangeDto("trainer1", "old", "new");

        when(validator.validate(dto)).thenReturn(Collections.emptySet());
        when(userService.authenticate(dto.getUsername(), dto.getOldPassword()))
                .thenReturn(new User());

        gymFacade.changeTrainerPassword(dto);

        verify(trainerService).changePassword(dto);
    }

    @Test
    void getUnassignedTrainers_shouldAuthenticateAndReturnList() {
        String username = "trainee1", password = "pass";
        TrainerDto dto = new TrainerDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainerService.getUnassignedTrainersForTrainee(username)).thenReturn(List.of(dto));

        List<TrainerDto> result = gymFacade.getUnassignedTrainers(username, password);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getTraineeTrainingsFiltered_shouldDelegateWithFilters() {
        String username = "trainee1", password = "pass";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        String trainerName = "Jane";
        String trainingType = "Yoga";

        TrainingDto dto = new TrainingDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainingService.getTrainingsForTrainee(username, password, from, to, trainerName, trainingType))
                .thenReturn(List.of(dto));

        List<TrainingDto> result = gymFacade.getTraineeTrainingsFiltered(username, password, from, to, trainerName, trainingType);
        assertEquals(1, result.size());
    }

    @Test
    void updateTraineeTrainers_shouldAuthenticateAndDelegate() {
        String username = "trainee1", password = "pass";
        List<Long> trainerIds = List.of(1L, 2L);

        when(userService.authenticate(username, password)).thenReturn(new User());

        gymFacade.updateTraineeTrainers(username, password, trainerIds);

        verify(traineeService).updateTrainersList(username, trainerIds);
    }
}

package facade;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.example.dto.*;
import org.example.facade.GymFacade;
import org.example.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private UserService userService;

    @Mock
    private Validator validator;

    @InjectMocks
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_validDto_delegatesAndReturns() {
        CreateTraineeDto dto = mock(CreateTraineeDto.class);
        TraineeDto returnedDto = mock(TraineeDto.class);


        when(validator.validate(dto)).thenReturn(Set.of());
        when(traineeService.createTrainee(dto)).thenReturn(returnedDto);

        TraineeDto result = gymFacade.createTrainee(dto);

        assertSame(returnedDto, result);
        verify(validator).validate(dto);
        verify(traineeService).createTrainee(dto);
    }

    @Test
    void createTrainee_invalidDto_throwsConstraintViolationException() {
        CreateTraineeDto dto = mock(CreateTraineeDto.class);
        ConstraintViolation<CreateTraineeDto> violation = mock(ConstraintViolation.class);

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () ->
                gymFacade.createTrainee(dto));

        assertTrue(exception.getConstraintViolations().contains(violation));
        verify(validator).validate(dto);
        verifyNoInteractions(traineeService);
    }

    @Test
    void getTrainee_delegatesToService() {
        String username = "user1";
        TraineeDto dto = mock(TraineeDto.class);

        when(traineeService.getByUsername(username)).thenReturn(dto);

        TraineeDto result = gymFacade.getTrainee(username);

        assertSame(dto, result);
        verify(traineeService).getByUsername(username);
        verifyNoInteractions(validator, trainerService, trainingService, userService);
    }

    @Test
    void addTraining_validDto_delegatesAndReturns() {
        CreateTrainingDto dto = mock(CreateTrainingDto.class);
        TrainingDto trainingDto = mock(TrainingDto.class);

        when(validator.validate(dto)).thenReturn(Set.of());
        when(trainingService.addTraining(dto)).thenReturn(trainingDto);

        TrainingDto result = gymFacade.addTraining(dto);

        assertSame(trainingDto, result);
        verify(validator).validate(dto);
        verify(trainingService).addTraining(dto);
    }

    @Test
    void changeTrainerPassword_invalidDto_throws() {
        PasswordChangeDto dto = mock(PasswordChangeDto.class);
        ConstraintViolation<PasswordChangeDto> violation = mock(ConstraintViolation.class);

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(ConstraintViolationException.class, () -> gymFacade.changeTrainerPassword(dto));

        verify(validator).validate(dto);
        verifyNoInteractions(trainerService);
    }

    @Test
    void toggleTrainerStatus_delegates() {
        String username = "trainer1";

        gymFacade.toggleTrainerStatus(username);

        verify(trainerService).toggleActive(username);
        verifyNoInteractions(validator, traineeService, trainingService, userService);
    }

    @Test
    void getTraineeTrainings_delegates() {
        String username = "trainee1";
        List<TrainingDto> list = List.of(mock(TrainingDto.class));

        when(trainingService.getTrainingsForTrainee(username)).thenReturn(list);

        List<TrainingDto> result = gymFacade.getTraineeTrainings(username);

        assertSame(list, result);
        verify(trainingService).getTrainingsForTrainee(username);
        verifyNoInteractions(validator, traineeService, trainerService, userService);
    }
}

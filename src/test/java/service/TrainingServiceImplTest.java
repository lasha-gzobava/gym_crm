package service;

import org.example.dto.training.TrainingAddDto;
import org.example.dto.training.TrainingDto;
import org.example.entity.*;
import org.example.mapper.TrainingMapper;
import org.example.repository.*;
import org.example.service.UserService;
import org.example.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock private TrainingRepository trainingRepository;
    @Mock private TrainerRepository trainerRepository;
    @Mock private TraineeRepository traineeRepository;
    @Mock private TrainingTypeRepository trainingTypeRepository;
    @Mock private TrainingMapper trainingMapper;
    @Mock private UserService userService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainer trainer;
    private Trainee trainee;
    private Training training;
    private TrainingDto trainingDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User trainerUser = new User("Trainer", "One", "trainer.one", "pass");
        trainerUser.setIsActive(true);
        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(new TrainingType(1L, "Yoga"));

        User traineeUser = new User("Trainee", "One", "trainee.one", "pwd");
        traineeUser.setIsActive(true);
        trainee = new Trainee();
        trainee.setUser(traineeUser);

        training = new Training();
        training.setTrainingId(1L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60L);
        training.setTrainingType(trainer.getSpecialization());

        trainingDto = new TrainingDto();
        trainingDto.setId(1L);
        trainingDto.setTrainingName("Morning Yoga");
        trainingDto.setTrainingDate(training.getTrainingDate());
        trainingDto.setTrainingDuration(60);
        trainingDto.setTrainerId(1L);
        trainingDto.setTraineeId(1L);
        trainingDto.setTrainingTypeId(1L);
    }

    @Test
    void addTraining_shouldSaveTraining() {
        TrainingAddDto dto = new TrainingAddDto("trainee.one", "trainer.one", "Morning Yoga", LocalDate.now(), 60L);

        when(userService.authenticate("trainer.one", "pass")).thenReturn(trainer.getUser());
        when(traineeRepository.findByUsername("trainee.one")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("trainer.one")).thenReturn(Optional.of(trainer));

        trainingService.addTraining(dto, "pass");

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void getTrainingsForTrainee_shouldReturnMappedList() {
        when(userService.authenticate("trainee.one", "pwd")).thenReturn(trainee.getUser());
        when(trainingRepository.findByTraineeUserUsername("trainee.one")).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainee("trainee.one", "pwd");

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
    }

    @Test
    void getTrainingsForTrainer_shouldReturnMappedList() {
        when(userService.authenticate("trainer.one", "pass")).thenReturn(trainer.getUser());
        when(trainingRepository.findByTrainerUserUsername("trainer.one")).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainer("trainer.one", "pass");

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTrainingName());
    }

    @Test
    void getTrainingsForTrainee_withFilters_shouldFilterCorrectly() {
        when(userService.authenticate("trainee.one", "pwd")).thenReturn(trainee.getUser());
        when(trainingRepository.findByTraineeUserUsername("trainee.one")).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainee(
                "trainee.one", "pwd", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "Trainer", "Yoga");

        assertEquals(1, result.size());
    }

    @Test
    void getTrainingsForTrainer_withFilters_shouldReturnFilteredList() {
        when(userService.authenticate("trainer.one", "pass")).thenReturn(trainer.getUser());
        when(trainingRepository.findByTrainerUserUsername("trainer.one")).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainer(
                "trainer.one", "pass", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "Trainee");

        assertEquals(1, result.size());
    }
}

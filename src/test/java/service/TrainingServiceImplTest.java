package service;

import org.example.dto.CreateTrainingDto;
import org.example.dto.TrainingDto;
import org.example.entity.*;
import org.example.mapper.TrainingMapper;
import org.example.repository.*;
import org.example.service.UserService;
import org.example.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_shouldCreateTrainingAndReturnDto() {
        CreateTrainingDto dto = new CreateTrainingDto();
        dto.setTrainingName("Morning Cardio");
        dto.setTrainerId(1L);
        dto.setTraineeId(2L);
        dto.setTrainingTypeId(3L);
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);
        dto.setTrainerUsername("trainer1");
        dto.setTrainerPassword("pass");

        Trainer trainer = new Trainer(); trainer.setTrainerId(1L);
        Trainee trainee = new Trainee(); trainee.setTraineeId(2L);
        TrainingType trainingType = new TrainingType(); trainingType.setTrainingTypeId(3L);
        User trainerUser = new User("T", "One", "trainer1", "pass");
        User traineeUser = new User("R", "Two", "trainee1", "pass");
        trainer.setUser(trainerUser); trainee.setUser(traineeUser);

        Training training = new Training(trainee, trainer, dto.getTrainingName(), trainingType, dto.getTrainingDate(), 60L);
        TrainingDto expectedDto = new TrainingDto();

        when(userService.authenticate("trainer1", "pass")).thenReturn(trainerUser);
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any())).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expectedDto);

        TrainingDto result = trainingService.addTraining(dto);
        assertEquals(expectedDto, result);
    }

    @Test
    void getTrainingsForTrainee_shouldReturnMappedTrainings() {
        String username = "trainee1", password = "pass";
        Training training = new Training();
        TrainingDto dto = new TrainingDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainingRepository.findByTraineeUserUsername(username)).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(dto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainee(username, password);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getTrainingsForTrainer_shouldReturnMappedTrainings() {
        String username = "trainer1", password = "pass";
        Training training = new Training();
        TrainingDto dto = new TrainingDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainingRepository.findByTrainerUserUsername(username)).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(dto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainer(username, password);
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getTrainingsForTrainee_withFilters_shouldReturnFilteredResults() {
        String username = "trainee1", password = "pass";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        User trainerUser = new User("Jane", "Doe", "jdoe", "pass");
        TrainingType type = new TrainingType(); type.setTrainingTypeName("Cardio");

        Training training = new Training();
        training.setTrainingDate(LocalDate.of(2024, 6, 1));
        training.setTrainer(new Trainer()); training.getTrainer().setUser(trainerUser);
        training.setTrainingType(type);

        TrainingDto dto = new TrainingDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainingRepository.findByTraineeUserUsername(username)).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(dto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainee(
                username, password, from, to, "Jane", "Cardio"
        );

        assertEquals(1, result.size());
    }

    @Test
    void getTrainingsForTrainer_withFilters_shouldReturnFilteredResults() {
        String username = "trainer1", password = "pass";
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        User traineeUser = new User("Sam", "Smith", "ssmith", "pass");

        Training training = new Training();
        training.setTrainingDate(LocalDate.of(2024, 5, 1));
        training.setTrainee(new Trainee()); training.getTrainee().setUser(traineeUser);

        TrainingDto dto = new TrainingDto();

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(trainingRepository.findByTrainerUserUsername(username)).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(dto);

        List<TrainingDto> result = trainingService.getTrainingsForTrainer(
                username, password, from, to, "Sam"
        );

        assertEquals(1, result.size());
    }
}

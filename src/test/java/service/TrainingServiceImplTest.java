package service;

import org.example.dto.CreateTrainingDto;
import org.example.dto.TrainingDto;
import org.example.entity.*;
import org.example.mapper.TrainingMapper;
import org.example.repository.*;
import org.example.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_success() {
        CreateTrainingDto dto = new CreateTrainingDto();
        dto.setTrainingName("Yoga Basics");
        dto.setTrainingDate(LocalDate.of(2025, 8, 1));
        dto.setTrainingDuration(60);
        dto.setTrainerId(1L);
        dto.setTraineeId(2L);
        dto.setTrainingTypeId(3L);

        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("trainer1");
        trainer.setUser(trainerUser);

        Trainee trainee = new Trainee();
        User traineeUser = new User();
        traineeUser.setUsername("trainee1");
        trainee.setUser(traineeUser);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");

        Training training = new Training(trainee, trainer, dto.getTrainingName(), trainingType, dto.getTrainingDate(), (long) dto.getTrainingDuration());

        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTrainingName(dto.getTrainingName());

        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));
        when(traineeRepository.findById(2L)).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(3L)).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        TrainingDto result = trainingService.addTraining(dto);

        assertNotNull(result);
        assertEquals("Yoga Basics", result.getTrainingName());
        verify(trainingRepository).save(any(Training.class));
        verify(trainingMapper).toDto(training);
    }


}

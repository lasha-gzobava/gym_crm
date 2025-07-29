package mapper;


import org.example.dto.TrainingDto;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.entity.TrainingType;
import org.example.mapper.TrainingMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private final TrainingMapper trainingMapper = new TrainingMapper();

    @Test
    void toDto_shouldMapAllFields() {
        Trainee trainee = new Trainee();
        trainee.setTraineeId(10L);

        Trainer trainer = new Trainer();
        trainer.setTrainerId(20L);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeId(30L);

        Training training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Strength Training");
        training.setTrainingDate(LocalDate.of(2024, 7, 29));
        training.setTrainingDuration(90L);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);

        TrainingDto dto = trainingMapper.toDto(training);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Strength Training", dto.getTrainingName());
        assertEquals(LocalDate.of(2024, 7, 29), dto.getTrainingDate());
        assertEquals(90, dto.getTrainingDuration());
        assertEquals(20L, dto.getTrainerId());
        assertEquals(10L, dto.getTraineeId());
        assertEquals(30L, dto.getTrainingTypeId());
    }

    @Test
    void toDto_shouldReturnNull_whenInputIsNull() {
        assertNull(trainingMapper.toDto(null));
    }
}

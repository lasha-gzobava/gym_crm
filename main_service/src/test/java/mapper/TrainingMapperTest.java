package mapper;

import org.example.dto.training.TrainingDto;
import org.example.entity.*;
import org.example.mapper.TrainingMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private final TrainingMapper trainingMapper = new TrainingMapper();

    @Test
    void toDto_shouldMapTrainingToDtoCorrectly() {
        // Arrange
        Training training = new Training();
        training.setTrainingId(1L);
        training.setTrainingName("Morning Session");
        training.setTrainingDate(LocalDate.of(2025, 8, 5));
        training.setTrainingDuration(90L);

        Trainer trainer = new Trainer();
        trainer.setTrainerId(10L);
        training.setTrainer(trainer);

        Trainee trainee = new Trainee();
        trainee.setTraineeId(20L);
        training.setTrainee(trainee);

        TrainingType type = new TrainingType();
        type.setTrainingTypeId(30L);
        training.setTrainingType(type);

        // Act
        TrainingDto dto = trainingMapper.toDto(training);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Morning Session", dto.getTrainingName());
        assertEquals(LocalDate.of(2025, 8, 5), dto.getTrainingDate());
        assertEquals(90, dto.getTrainingDuration());
        assertEquals(10L, dto.getTrainerId());
        assertEquals(20L, dto.getTraineeId());
        assertEquals(30L, dto.getTrainingTypeId());
    }

    @Test
    void toDto_nullInput_shouldReturnNull() {
        assertNull(trainingMapper.toDto(null));
    }
}

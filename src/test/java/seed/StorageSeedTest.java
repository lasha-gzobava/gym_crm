package seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entity.Trainee;
import org.example.entity.Trainer;
import org.example.entity.Training;
import org.example.seed.StorageSeed;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

class StorageSeedTest {

    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingService trainingService;
    private UserService userService;

    private StorageSeed storageSeed;

    private String tempJsonPath = "temp_seed.json";

    @BeforeEach
    void setup() throws Exception {
        // Mocks
        traineeService = mock(TraineeService.class);
        trainerService = mock(TrainerService.class);
        trainingService = mock(TrainingService.class);
        userService = mock(UserService.class);

        // Create temp file safely
        File tempFile = File.createTempFile("seed-", ".json");
        tempJsonPath = tempFile.getAbsolutePath();
        tempFile.deleteOnExit();

        // Set field using reflection
        storageSeed = new StorageSeed(traineeService, trainerService, trainingService, userService);
        Field field = StorageSeed.class.getDeclaredField("seedFilePath");
        field.setAccessible(true);
        field.set(storageSeed, tempJsonPath);
    }


    @Test
    void testSeedDataBase() throws Exception {
        // Arrange
        String json = """
        {
          "trainees": [
            {
              "firstName": "Alice",
              "lastName": "Smith",
              "dateOfBirth": "1990-01-01",
              "address": "123 Main St"
            }
          ],
          "trainers": [
            {
              "firstName": "Bob",
              "lastName": "Brown",
              "specialization": "Strength"
            }
          ],
          "trainings": [
            {
              "traineeId": 1,
              "trainerId": 2,
              "trainingName": "Morning Workout",
              "trainingType": "Cardio",
              "trainingDate": "2025-08-01",
              "trainingDurationMinutes": 45
            }
          ]
        }
        """;

        // Save to temp file
        try (FileWriter writer = new FileWriter(tempJsonPath)) {
            writer.write(json);
        }

        // Mock return objects
        when(traineeService.createTrainee(any(), any(), any(), any()))
                .thenReturn(new Trainee(1L, "1990-01-01", "123 Main St", 100L));
        when(trainerService.createTrainer(any(), any(), any()))
                .thenReturn(new Trainer(2L, "Strength", 200L));
        when(trainingService.createTraining(anyLong(), anyLong(), any(), any(), any(), any()))
                .thenReturn(new Training(3L, 1L, 2L, "Morning Workout", "Cardio", "2025-08-01", Duration.ofMinutes(45)));

        // Act
        storageSeed.seedDataBase();

        // Assert: verify service calls
        verify(traineeService).createTrainee("Alice", "Smith", "1990-01-01", "123 Main St");
        verify(trainerService).createTrainer("Bob", "Brown", "Strength");
        verify(trainingService).createTraining(1L, 2L, "Morning Workout", "Cardio", "2025-08-01", Duration.ofMinutes(45));

        // Cleanup
        new File(tempJsonPath).delete();
    }
}

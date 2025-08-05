
package controller;

import org.example.controller.TrainerController;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.*;
import org.example.dto.training.TrainerTrainingResponseDto;
import org.example.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerControllerTest {

    @InjectMocks
    private TrainerController trainerController;

    @Mock
    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnCreated_WhenValid() {
        TrainerCreateDto dto = new TrainerCreateDto("John", "Doe", "Strength");
        TraineeCredentialsDto creds = new TraineeCredentialsDto("john.doe", "secret");

        when(trainerService.registerWithCredentials(dto)).thenReturn(creds);

        ResponseEntity<TraineeCredentialsDto> response = trainerController.register(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(creds, response.getBody());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenRuntimeExceptionThrown() {
        TrainerCreateDto dto = new TrainerCreateDto("John", "Doe", "Strength");

        when(trainerService.registerWithCredentials(dto)).thenThrow(RuntimeException.class);

        ResponseEntity<TraineeCredentialsDto> response = trainerController.register(dto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getTrainer_ShouldReturnProfile_WhenValid() {
        TrainerProfileDto profile = new TrainerProfileDto("jane", "Jane", "Smith", "Cardio", true, List.of());

        when(trainerService.getTrainerProfile("jane", "pwd")).thenReturn(profile);

        ResponseEntity<TrainerProfileDto> response = trainerController.getTrainer("jane", "pwd");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profile, response.getBody());
    }

    @Test
    void getTrainer_ShouldReturnNotFound_WhenInvalid() {
        when(trainerService.getTrainerProfile("jane", "pwd")).thenThrow(RuntimeException.class);

        ResponseEntity<TrainerProfileDto> response = trainerController.getTrainer("jane", "pwd");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateTrainer_ShouldReturnUpdatedProfile_WhenValid() {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane", "Jane", "Smith", "Yoga", true);
        TrainerProfileDto profile = new TrainerProfileDto("jane", "Jane", "Smith", "Yoga", true, List.of());

        when(trainerService.updateTrainerProfile(dto, "pwd")).thenReturn(profile);

        ResponseEntity<TrainerProfileDto> response = trainerController.updateTrainer(dto, "pwd");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profile, response.getBody());
    }

    @Test
    void updateTrainer_ShouldReturnNotFound_WhenInvalid() {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane", "Jane", "Smith", "Yoga", true);

        when(trainerService.updateTrainerProfile(dto, "pwd")).thenThrow(RuntimeException.class);

        ResponseEntity<TrainerProfileDto> response = trainerController.updateTrainer(dto, "pwd");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUnassignedTrainers_ShouldReturnList_WhenValid() {
        List<TrainerForTrainerListDto> list = Collections.singletonList(
            new TrainerForTrainerListDto("john", "John", "Doe", "Strength")
        );

        when(trainerService.getUnassignedTrainersForTrainee("trainee1", "pwd")).thenReturn(list);

        ResponseEntity<List<TrainerForTrainerListDto>> response = trainerController.getUnassignedTrainers("trainee1", "pwd");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void getUnassignedTrainers_ShouldReturnNotFound_WhenInvalid() {
        when(trainerService.getUnassignedTrainersForTrainee("trainee1", "pwd")).thenThrow(RuntimeException.class);

        ResponseEntity<List<TrainerForTrainerListDto>> response = trainerController.getUnassignedTrainers("trainee1", "pwd");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getTrainerTrainings_ShouldReturnList_WhenValid() {
        List<TrainerTrainingResponseDto> list = List.of(
                new TrainerTrainingResponseDto("Workout", LocalDate.now(), "Cardio", 60L, "Trainee Name")
        );
        when(trainerService.getTrainerTrainingsList(any(), eq("pwd"))).thenReturn(list);

        ResponseEntity<List<TrainerTrainingResponseDto>> response = trainerController.getTrainerTrainings("trainer1", null, null, null, "pwd");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(list, response.getBody());
    }

    @Test
    void getTrainerTrainings_ShouldReturnNotFound_WhenInvalid() {
        when(trainerService.getTrainerTrainingsList(any(), eq("pwd"))).thenThrow(RuntimeException.class);

        ResponseEntity<List<TrainerTrainingResponseDto>> response = trainerController.getTrainerTrainings("trainer1", null, null, null, "pwd");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void toggleTrainerActive_ShouldReturnSuccess_WhenValid() {
        TrainerActivationDto dto = new TrainerActivationDto("trainer1", true);

        ResponseEntity<String> response = trainerController.toggleTrainerActive(dto, "pwd");

        verify(trainerService).toggleActive("trainer1", true, "pwd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void toggleTrainerActive_ShouldReturnNotFound_WhenError() {
        TrainerActivationDto dto = new TrainerActivationDto("trainer1", true);
        doThrow(RuntimeException.class).when(trainerService).toggleActive(any(), anyBoolean(), any());

        ResponseEntity<String> response = trainerController.toggleTrainerActive(dto, "pwd");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

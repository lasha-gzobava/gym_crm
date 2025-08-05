package controller;

import org.example.controller.TraineeController;
import org.example.dto.login.PasswordChangeDto;
import org.example.dto.trainee.*;
import org.example.dto.trainer.TrainerForTrainerListDto;
import org.example.dto.training.TraineeTrainingRequestDto;
import org.example.dto.training.TraineeTrainingResponseDto;
import org.example.entity.User;
import org.example.service.TraineeService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_success() {
        TraineeCreateDto dto = new TraineeCreateDto();
        dto.setUser(new org.example.dto.user.UserCreateDto("John", "Doe"));
        TraineeCredentialsDto response = new TraineeCredentialsDto("john.doe", "pass");

        when(traineeService.registerWithCredentials(any())).thenReturn(response);

        ResponseEntity<TraineeCredentialsDto> result = traineeController.register(dto);
        assertEquals(201, result.getStatusCodeValue());
        assertEquals("john.doe", result.getBody().getUsername());
    }

    @Test
    void getProfile_success() {
        String username = "john";
        String password = "pass";

        when(userService.authenticate(username, password)).thenReturn(new User());
        when(traineeService.getTraineeProfile(username, password)).thenReturn(
                new TraineeProfileDto("John", "Doe", LocalDate.now(), "Address", true, Collections.emptyList())
        );

        ResponseEntity<TraineeProfileDto> result = traineeController.getProfile(username, password);
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void getProfile_failure() {
        when(userService.authenticate(any(), any())).thenThrow(new RuntimeException("Auth fail"));

        ResponseEntity<TraineeProfileDto> result = traineeController.getProfile("x", "y");
        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    void updateProfile_success() {
        TraineeProfileUpdateDto dto = new TraineeProfileUpdateDto();
        dto.setUsername("john");

        when(traineeService.updateProfile(eq(dto), anyString())).thenReturn(
                new TraineeProfileDto("John", "Doe", LocalDate.now(), "Address", true, Collections.emptyList())
        );

        ResponseEntity<TraineeProfileDto> result = traineeController.updateProfile(dto, "pass");
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void updateProfile_failure() {
        when(traineeService.updateProfile(any(), any())).thenThrow(new RuntimeException("not found"));

        ResponseEntity<TraineeProfileDto> result = traineeController.updateProfile(new TraineeProfileUpdateDto(), "pass");
        assertEquals(404, result.getStatusCodeValue());
    }

    @Test
    void deleteProfile_success() {
        TraineeDeleteRequestDto dto = new TraineeDeleteRequestDto("john", "pass");
        doNothing().when(traineeService).deleteByUsername(anyString(), anyString());

        ResponseEntity<String> response = traineeController.deleteProfile(dto, "pass");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void deleteProfile_failure_not_found() {
        doThrow(new RuntimeException("fail")).when(traineeService).deleteByUsername(any(), any());

        ResponseEntity<String> response = traineeController.deleteProfile(new TraineeDeleteRequestDto("john", "pass"), "pass");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void updateTrainers_success() {
        TraineeTrainerUpdateDto dto = new TraineeTrainerUpdateDto();
        dto.setTraineeUsername("john");

        when(traineeService.updateTraineeTrainers(eq(dto), anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<List<TrainerForTrainerListDto>> response = traineeController.updateTraineeTrainers(dto, "pass");
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateTrainers_failure() {
        when(traineeService.updateTraineeTrainers(any(), any())).thenThrow(new RuntimeException("fail"));
        ResponseEntity<List<TrainerForTrainerListDto>> response = traineeController.updateTraineeTrainers(new TraineeTrainerUpdateDto(), "pass");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void getTrainings_invalidDateFormat() {
        ResponseEntity<List<TraineeTrainingResponseDto>> result = traineeController.getTraineeTrainings(
                "john", "invalid-date", null, null, null, "pass");
        assertEquals(400, result.getStatusCodeValue());
    }

    @Test
    void toggleTraineeActive_success() {
        TraineeActivationDto dto = new TraineeActivationDto("john", true);
        doNothing().when(traineeService).toggleActive(any(), anyBoolean(), any());

        ResponseEntity<String> result = traineeController.toggleTraineeActive(dto, "pass");
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void toggleTraineeActive_failure() {
        doThrow(new RuntimeException("fail")).when(traineeService).toggleActive(any(), anyBoolean(), any());

        ResponseEntity<String> result = traineeController.toggleTraineeActive(
                new TraineeActivationDto("john", false), "pass");
        assertEquals(404, result.getStatusCodeValue());
    }
}

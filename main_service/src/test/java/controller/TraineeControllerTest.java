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
import org.example.util.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TraineeController traineeController;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(traineeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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
    void getProfile_failure() throws Exception {
        when(userService.authenticate(anyString(), anyString()))
                .thenThrow(new RuntimeException("Auth fail"));

        mockMvc.perform(get("/trainee/profile")
                        .param("username", "x")
                        .param("password", "y"))
                .andExpect(status().isNotFound());
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
    void updateProfile_validationError_returns400_fromAdvice() throws Exception {
        // service won't be called because validation fails first
        mockMvc.perform(put("/trainee/profile")
                        .param("password", "pass")
                        .contentType("application/json")
                        .content("{\"username\":\"john\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProfile_success() {
        TraineeDeleteRequestDto dto = new TraineeDeleteRequestDto("john", "pass");
        doNothing().when(traineeService).deleteByUsername(anyString(), anyString());

        ResponseEntity<String> response = traineeController.deleteProfile(dto);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void deleteProfile_failure_not_found_fromAdvice() throws Exception {
        doThrow(new RuntimeException("fail"))
                .when(traineeService).deleteByUsername(any(), any());

        String json = """
      {
        "username": "john",
        "password": "pass"
      }
      """;

        mockMvc.perform(delete("/trainee")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
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
    void updateTrainers_failure_fromAdvice() throws Exception {
        // given
        doThrow(new RuntimeException("fail"))
                .when(traineeService).updateTraineeTrainers(any(), any());

        String json = """
    {
      "traineeUsername": "john",
      "trainerUsernames": ["t1","t2"]
    }
    """;

        mockMvc.perform(put("/trainee/trainers")
                        .param("password", "pass")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isNotFound());
    }


    @Test
    void getTrainings_invalidDateFormat() throws Exception {
        mockMvc.perform(get("/trainee/trainings")
                        .param("username", "john")
                        .param("periodFrom", "invalid-date")   // <-- correct name
                        .param("password", "pass"))
                .andExpect(status().isBadRequest());
    }



    @Test
    void toggleTraineeActive_success() {

        TraineeActivationDto dto = new TraineeActivationDto("john", true);
        when(traineeService.toggleActive(anyString(), anyBoolean(), anyString()))
                .thenReturn(true); // <- returns boolean, not void


        ResponseEntity<String> result = traineeController.toggleTraineeActive(dto, "pass");


        assertEquals(200, result.getStatusCodeValue());
    }



    @Test
    void toggleTraineeActive_failure() throws Exception {
        when(traineeService.toggleActive(anyString(), anyBoolean(), anyString()))
                .thenThrow(new RuntimeException("fail"));

        mockMvc.perform(patch("/trainee/activate")
                        .param("password", "pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"john\",\"isActive\":false}"))
                .andExpect(status().isNotFound());
    }
}

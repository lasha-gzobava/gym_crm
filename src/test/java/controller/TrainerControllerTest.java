package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.controller.TrainerController;
import org.example.dto.trainee.TraineeCredentialsDto;
import org.example.dto.trainer.*;
import org.example.dto.training.TrainerTrainingRequestDto;
import org.example.dto.training.TrainerTrainingResponseDto;
import org.example.service.TrainerService;
import org.example.util.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainerControllerTest {

    private MockMvc mockMvc;
    private TrainerService trainerService;
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        trainerService = mock(TrainerService.class);
        TrainerController controller = new TrainerController(trainerService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler()) // <-- your handler
                .build();

        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // for LocalDate in JSON
    }

    /* -------------------- /trainer/register -------------------- */

    @Test
    void register_ShouldReturnCreated_WhenValid() throws Exception {
        TrainerCreateDto dto = new TrainerCreateDto("John", "Doe", "Strength");
        TraineeCredentialsDto creds = new TraineeCredentialsDto("john.doe", "secret");

        when(trainerService.registerWithCredentials(dto)).thenReturn(creds);

        mockMvc.perform(post("/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(om.writeValueAsString(creds)));
    }

    @Test
    void register_ShouldReturnNotFound_WhenRuntimeException() throws Exception {
        TrainerCreateDto dto = new TrainerCreateDto("John", "Doe", "Strength");
        when(trainerService.registerWithCredentials(dto)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(post("/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isNotFound()); // per your handler mapping RuntimeException -> 404
    }

    /* -------------------- /trainer/profile (GET/PUT) -------------------- */

    @Test
    void getTrainer_ShouldReturnProfile_WhenValid() throws Exception {
        TrainerProfileDto profile =
                new TrainerProfileDto("jane", "Jane", "Smith", "Cardio", true, List.of());

        when(trainerService.getTrainerProfile("jane", "pwd")).thenReturn(profile);

        mockMvc.perform(get("/trainer/profile")
                        .param("username", "jane")
                        .param("password", "pwd"))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(profile)));
    }

    @Test
    void getTrainer_ShouldReturnNotFound_WhenRuntimeException() throws Exception {
        when(trainerService.getTrainerProfile("jane", "pwd")).thenThrow(new RuntimeException("nope"));

        mockMvc.perform(get("/trainer/profile")
                        .param("username", "jane")
                        .param("password", "pwd"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTrainer_ShouldReturnUpdatedProfile_WhenValid() throws Exception {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane", "Jane", "Smith", "Yoga", true);
        TrainerProfileDto profile =
                new TrainerProfileDto("jane", "Jane", "Smith", "Yoga", true, List.of());

        when(trainerService.updateTrainerProfile(dto, "pwd")).thenReturn(profile);

        mockMvc.perform(put("/trainer/profile")
                        .param("password", "pwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(profile)));
    }

    @Test
    void updateTrainer_ShouldReturnNotFound_WhenRuntimeException() throws Exception {
        TrainerUpdateDto dto = new TrainerUpdateDto("jane", "Jane", "Smith", "Yoga", true);
        when(trainerService.updateTrainerProfile(dto, "pwd")).thenThrow(new RuntimeException("nope"));

        mockMvc.perform(put("/trainer/profile")
                        .param("password", "pwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    /* -------------------- /trainer/unassigned -------------------- */

    @Test
    void getUnassignedTrainers_ShouldReturnList_WhenValid() throws Exception {
        List<TrainerForTrainerListDto> list = Collections.singletonList(
                new TrainerForTrainerListDto("john", "John", "Doe", "Strength")
        );

        when(trainerService.getUnassignedTrainersForTrainee("trainee1", "pwd")).thenReturn(list);

        mockMvc.perform(get("/trainer/unassigned")
                        .param("username", "trainee1")
                        .param("password", "pwd"))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(list)));
    }

    @Test
    void getUnassignedTrainers_ShouldReturnNotFound_WhenRuntimeException() throws Exception {
        when(trainerService.getUnassignedTrainersForTrainee("trainee1", "pwd"))
                .thenThrow(new RuntimeException("no trainee"));

        mockMvc.perform(get("/trainer/unassigned")
                        .param("username", "trainee1")
                        .param("password", "pwd"))
                .andExpect(status().isNotFound());
    }

    /* -------------------- /trainer/trainings -------------------- */

    @Test
    void getTrainerTrainings_ShouldReturnList_WhenValid() throws Exception {
        List<TrainerTrainingResponseDto> list = List.of(
                new TrainerTrainingResponseDto("Workout", LocalDate.of(2025, 8, 12), "Cardio", 60L, "Trainee Name")
        );

        when(trainerService.getTrainerTrainingsList(
                ArgumentMatchers.any(TrainerTrainingRequestDto.class), eq("pwd"))).thenReturn(list);

        mockMvc.perform(get("/trainer/trainings")
                        .param("username", "trainer1")
                        .param("password", "pwd"))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(list)));
    }

    @Test
    void getTrainerTrainings_ShouldReturnNotFound_WhenRuntimeException() throws Exception {
        when(trainerService.getTrainerTrainingsList(any(), eq("pwd")))
                .thenThrow(new RuntimeException("no trainings"));

        mockMvc.perform(get("/trainer/trainings")
                        .param("username", "trainer1")
                        .param("password", "pwd"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTrainerTrainings_ShouldReturnBadRequest_WhenInvalidDate() throws Exception {
        // Your handler has a dedicated DateTimeParseException mapping -> 400 + plain message
        mockMvc.perform(get("/trainer/trainings")
                        .param("username", "trainer1")
                        .param("password", "pwd")
                        .param("periodFrom", "12-08-2025")) // invalid (expects ISO yyyy-MM-dd)
                .andExpect(status().isBadRequest());
    }

    /* -------------------- /trainer/activate -------------------- */

    @Test
    void toggleTrainerActive_ShouldReturnOk_WhenValid() throws Exception {
        TrainerActivationDto dto = new TrainerActivationDto("trainer1", true);

        mockMvc.perform(patch("/trainer/activate")
                        .param("password", "pwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainer trainer1 is now active"));

        verify(trainerService).toggleActive("trainer1", true, "pwd");
    }

    @Test
    void toggleTrainerActive_ShouldReturnNotFound_WhenRuntimeException() throws Exception {
        TrainerActivationDto dto = new TrainerActivationDto("trainer1", true);
        doThrow(new RuntimeException("not found"))
                .when(trainerService).toggleActive(any(), anyBoolean(), any());

        mockMvc.perform(patch("/trainer/activate")
                        .param("password", "pwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}

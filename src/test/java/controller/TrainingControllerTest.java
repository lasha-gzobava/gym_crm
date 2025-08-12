package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.controller.TrainingController;
import org.example.dto.training.TrainingAddDto;
import org.example.service.TrainingService;
import org.example.util.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingControllerTest {

    private MockMvc mockMvc;
    private TrainingService trainingService;
    private ObjectMapper om;

    @BeforeEach
    void setUp() {
        trainingService = Mockito.mock(TrainingService.class);
        TrainingController controller = new TrainingController(trainingService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule()); // for LocalDate
    }

    private TrainingAddDto validDto() {
        TrainingAddDto dto = new TrainingAddDto();
        // Fill all required fields per your validator messages:
        // trainingName, trainingDate, trainingDuration, trainerUsername, traineeUsername
        dto.setTrainingName("Workout A");
        dto.setTrainingDate(LocalDate.of(2025, 8, 12));
        dto.setTrainingDuration(60L);
        dto.setTrainerUsername("trainer1");
        dto.setTraineeUsername("trainee1");
        return dto;
    }

    @Test
    void addTraining_shouldReturnOk() throws Exception {
        doNothing().when(trainingService).addTraining(any(TrainingAddDto.class), eq("pass"));

        mockMvc.perform(post("/training/add")
                        .param("password", "pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(validDto())))
                .andExpect(status().isOk())
                .andExpect(content().string("Training successfully added"));
    }

    @Test
    void addTraining_shouldReturnNotFound_whenServiceThrowsRuntimeException() throws Exception {
        doThrow(new RuntimeException("boom"))
                .when(trainingService).addTraining(any(TrainingAddDto.class), eq("pass"));

        mockMvc.perform(post("/training/add")
                        .param("password", "pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(validDto())))
                // Your GlobalExceptionHandler maps RuntimeException -> 404
                .andExpect(status().isNotFound());
    }

    @Test
    void addTraining_shouldReturnBadRequest_onValidationErrors() throws Exception {
        // Empty body triggers your validator messages for all required fields
        String invalidJson = "{}";

        mockMvc.perform(post("/training/add")
                        .param("password", "pass")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
        // If you want to assert the error body details, add jsonPath checks here.
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenMissingPasswordParam() throws Exception {
        mockMvc.perform(post("/training/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(validDto())))
                .andExpect(status().isBadRequest());
    }
}

package controller;

import org.example.controller.TrainingTypeController;
import org.example.service.TrainingTypeService;
import org.example.util.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TrainingTypeControllerTest {

    private MockMvc mockMvc;
    private TrainingTypeService trainingTypeService;

    @BeforeEach
    void setUp() {
        trainingTypeService = Mockito.mock(TrainingTypeService.class);
        TrainingTypeController controller = new TrainingTypeController(trainingTypeService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllTrainingTypes_shouldReturnOk_withEmptyList() throws Exception {
        when(trainingTypeService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]")); // <= no jsonPath needed
    }


    @Test
    void getAllTrainingTypes_shouldReturnNotFound_whenServiceThrowsRuntimeException() throws Exception {
        when(trainingTypeService.getAll()).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/training-types"))
                // per your GlobalExceptionHandler: RuntimeException -> 404
                .andExpect(status().isNotFound());
    }
}

package controller;

import org.example.controller.TrainingController;
import org.example.dto.training.TrainingAddDto;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTraining_shouldReturnOkResponse() {
        TrainingAddDto dto = new TrainingAddDto();
        String password = "pass";

        doNothing().when(trainingService).addTraining(dto, password);

        ResponseEntity<String> response = trainingController.addTraining(dto, password);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Training successfully added", response.getBody());
        verify(trainingService).addTraining(dto, password);
    }

    @Test
    void addTraining_shouldReturnBadRequestOnError() {
        TrainingAddDto dto = new TrainingAddDto();
        String password = "pass";

        doThrow(new RuntimeException("Something went wrong")).when(trainingService).addTraining(dto, password);

        ResponseEntity<String> response = trainingController.addTraining(dto, password);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Something went wrong"));
    }
}
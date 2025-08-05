package controller;

import org.example.controller.TrainingTypeController;
import org.example.dto.trainingtype.TrainingTypeDto;
import org.example.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeControllerTest {

    private TrainingTypeService trainingTypeService;
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        trainingTypeService = mock(TrainingTypeService.class);
        trainingTypeController = new TrainingTypeController(trainingTypeService);
    }

    @Test
    void getAllTrainingTypes_ShouldReturnListOfTypes() {
        // Arrange
        List<TrainingTypeDto> mockTypes = List.of(
                new TrainingTypeDto(1L, "Cardio"),
                new TrainingTypeDto(2L, "Strength")
        );

        when(trainingTypeService.getAll()).thenReturn(mockTypes);

        // Act
        ResponseEntity<List<TrainingTypeDto>> response = trainingTypeController.getAllTrainingTypes();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Cardio", response.getBody().get(0).getTrainingType());
        verify(trainingTypeService, times(1)).getAll();
    }
}

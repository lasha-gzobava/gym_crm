package service;

import org.example.dto.trainingtype.TrainingTypeDto;
import org.example.entity.TrainingType;
import org.example.repository.TrainingTypeRepository;
import org.example.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_shouldReturnAllTrainingTypesAsDtoList() {
        // Arrange
        TrainingType t1 = new TrainingType();
        t1.setTrainingTypeId(1L);
        t1.setTrainingTypeName("Yoga");

        TrainingType t2 = new TrainingType();
        t2.setTrainingTypeId(2L);
        t2.setTrainingTypeName("Pilates");

        when(trainingTypeRepository.findAll()).thenReturn(List.of(t1, t2));

        // Act
        List<TrainingTypeDto> result = trainingTypeService.getAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Yoga", result.get(0).getTrainingType());
        assertEquals("Pilates", result.get(1).getTrainingType());
    }
}

package org.example.trainerworkloadms.service;

import org.example.trainerworkloadms.dto.TrainingEventRequest;
import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.entity.Month;
import org.example.trainerworkloadms.entity.Trainer;
import org.example.trainerworkloadms.entity.Year;
import org.example.trainerworkloadms.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadService Tests")
class WorkloadServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private WorkloadService workloadService;

    private TrainingEventRequest request;
    private Trainer existingTrainer;

    @BeforeEach
    void setUp() {
        request = new TrainingEventRequest();
        request.setUsername("john.doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.of(2025, 3, 15));
        request.setDurationMinutes(60);
        request.setAction(TrainingEventRequest.ActionType.ADD);

        existingTrainer = new Trainer();
        existingTrainer.setTrainerId(1L);
        existingTrainer.setUsername("john.doe");
        existingTrainer.setFirstName("John");
        existingTrainer.setLastName("Doe");
        existingTrainer.setActive(true);
        existingTrainer.setYears(new ArrayList<>());
    }

    @Test
    @DisplayName("Should create new trainer when trainer doesn't exist")
    void testApply_CreateNewTrainer() {
        // Given
        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.empty());
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            trainer.setTrainerId(1L);
            return trainer;
        });

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("john.doe");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getYears()).hasSize(1);
        assertThat(response.getYears().get(0).getYear()).isEqualTo(2025);
        assertThat(response.getYears().get(0).getMonths()).hasSize(1);
        assertThat(response.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(3);
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(60);

        verify(trainerRepository).findByUsername("john.doe");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should add duration to existing trainer with existing year and month")
    void testApply_AddDurationToExistingTrainer() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(existingTrainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(3);
        month.setDuration(30);
        month.setYear(year);

        year.getMonths().add(month);
        existingTrainer.getYears().add(year);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears()).hasSize(1);
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(90); // 30 + 60

        verify(trainerRepository).findByUsername("john.doe");
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should create new year when year doesn't exist")
    void testApply_CreateNewYear() {
        // Given
        Year existingYear = new Year();
        existingYear.setYear(2024);
        existingYear.setTrainer(existingTrainer);
        existingYear.setMonths(new ArrayList<>());
        existingTrainer.getYears().add(existingYear);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response.getYears()).hasSize(2);
        assertThat(response.getYears().stream().anyMatch(y -> y.getYear() == 2025)).isTrue();
        assertThat(response.getYears().stream()
                .filter(y -> y.getYear() == 2025)
                .findFirst()
                .get()
                .getMonths()).hasSize(1);
    }

    @Test
    @DisplayName("Should create new month when month doesn't exist in year")
    void testApply_CreateNewMonth() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(existingTrainer);
        year.setMonths(new ArrayList<>());

        Month existingMonth = new Month();
        existingMonth.setMonth(2);
        existingMonth.setDuration(45);
        existingMonth.setYear(year);
        year.getMonths().add(existingMonth);

        existingTrainer.getYears().add(year);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response.getYears().get(0).getMonths()).hasSize(2);
        assertThat(response.getYears().get(0).getMonths().stream().anyMatch(m -> m.getMonth() == 3)).isTrue();
    }

    @Test
    @DisplayName("Should delete duration from existing workload")
    void testApply_DeleteDuration() {
        // Given
        request.setAction(TrainingEventRequest.ActionType.DELETE);
        request.setDurationMinutes(20);

        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(existingTrainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(3);
        month.setDuration(60);
        month.setYear(year);

        year.getMonths().add(month);
        existingTrainer.getYears().add(year);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(40); // 60 - 20

        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    @DisplayName("Should not allow negative duration when deleting more than available")
    void testApply_DeleteDurationWithNegativeResult() {
        // Given
        request.setAction(TrainingEventRequest.ActionType.DELETE);
        request.setDurationMinutes(100);

        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(existingTrainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(3);
        month.setDuration(60);
        month.setYear(year);

        year.getMonths().add(month);
        existingTrainer.getYears().add(year);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(0); // Max(0, 60-100) = 0
    }

    @Test
    @DisplayName("Should update trainer information on each apply")
    void testApply_UpdateTrainerInfo() {
        // Given
        existingTrainer.setFirstName("OldFirstName");
        existingTrainer.setLastName("OldLastName");
        existingTrainer.setActive(false);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(trainerCaptor.capture());

        Trainer savedTrainer = trainerCaptor.getValue();
        assertThat(savedTrainer.getFirstName()).isEqualTo("John");
        assertThat(savedTrainer.getLastName()).isEqualTo("Doe");
        assertThat(savedTrainer.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should retrieve trainer by username")
    void testGetTrainer_Found() {
        // Given
        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));

        // When
        Optional<TrainingEventResponse> result = workloadService.getTrainer("john.doe");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("john.doe");
        assertThat(result.get().getFirstName()).isEqualTo("John");
        assertThat(result.get().getLastName()).isEqualTo("Doe");

        verify(trainerRepository).findByUsername("john.doe");
    }

    @Test
    @DisplayName("Should return empty optional when trainer not found")
    void testGetTrainer_NotFound() {
        // Given
        when(trainerRepository.findByUsername("unknown.user")).thenReturn(Optional.empty());

        // When
        Optional<TrainingEventResponse> result = workloadService.getTrainer("unknown.user");

        // Then
        assertThat(result).isEmpty();

        verify(trainerRepository).findByUsername("unknown.user");
    }

    @Test
    @DisplayName("Should handle multiple months in same year")
    void testApply_MultipleMonthsInSameYear() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(existingTrainer);
        year.setMonths(new ArrayList<>());

        Month month1 = new Month();
        month1.setMonth(1);
        month1.setDuration(100);
        month1.setYear(year);

        Month month2 = new Month();
        month2.setMonth(2);
        month2.setDuration(150);
        month2.setYear(year);

        year.getMonths().add(month1);
        year.getMonths().add(month2);
        existingTrainer.getYears().add(year);

        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        // When
        TrainingEventResponse response = workloadService.apply(request);

        // Then
        assertThat(response.getYears().get(0).getMonths()).hasSize(3);
        assertThat(response.getYears().get(0).getMonths().stream()
                .filter(m -> m.getMonth() == 1)
                .findFirst()
                .get()
                .getDuration()).isEqualTo(100);
        assertThat(response.getYears().get(0).getMonths().stream()
                .filter(m -> m.getMonth() == 2)
                .findFirst()
                .get()
                .getDuration()).isEqualTo(150);
        assertThat(response.getYears().get(0).getMonths().stream()
                .filter(m -> m.getMonth() == 3)
                .findFirst()
                .get()
                .getDuration()).isEqualTo(60);
    }
}
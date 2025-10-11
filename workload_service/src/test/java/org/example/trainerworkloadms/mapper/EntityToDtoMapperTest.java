package org.example.trainerworkloadms.mapper;

import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.entity.Month;
import org.example.trainerworkloadms.entity.Trainer;
import org.example.trainerworkloadms.entity.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EntityToDtoMapper Tests")
class EntityToDtoMapperTest {

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setTrainerId(1L);
        trainer.setUsername("john.doe");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setActive(true);
        trainer.setYears(new ArrayList<>());
    }

    @Test
    @DisplayName("Should map trainer with single year and single month")
    void testToResponse_SingleYearSingleMonth() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(3);
        month.setDuration(60);
        month.setYear(year);

        year.getMonths().add(month);
        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

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
    }

    @Test
    @DisplayName("Should map trainer with multiple years")
    void testToResponse_MultipleYears() {
        // Given
        Year year2024 = new Year();
        year2024.setYear(2024);
        year2024.setTrainer(trainer);
        year2024.setMonths(new ArrayList<>());

        Month month2024 = new Month();
        month2024.setMonth(12);
        month2024.setDuration(90);
        month2024.setYear(year2024);
        year2024.getMonths().add(month2024);

        Year year2025 = new Year();
        year2025.setYear(2025);
        year2025.setTrainer(trainer);
        year2025.setMonths(new ArrayList<>());

        Month month2025 = new Month();
        month2025.setMonth(1);
        month2025.setDuration(120);
        month2025.setYear(year2025);
        year2025.getMonths().add(month2025);

        trainer.getYears().add(year2024);
        trainer.getYears().add(year2025);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears()).hasSize(2);

        assertThat(response.getYears().get(0).getYear()).isEqualTo(2024);
        assertThat(response.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(12);
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(90);

        assertThat(response.getYears().get(1).getYear()).isEqualTo(2025);
        assertThat(response.getYears().get(1).getMonths().get(0).getMonth()).isEqualTo(1);
        assertThat(response.getYears().get(1).getMonths().get(0).getDuration()).isEqualTo(120);
    }

    @Test
    @DisplayName("Should map trainer with multiple months in a year")
    void testToResponse_MultipleMonthsInYear() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        Month month1 = new Month();
        month1.setMonth(1);
        month1.setDuration(60);
        month1.setYear(year);

        Month month2 = new Month();
        month2.setMonth(2);
        month2.setDuration(90);
        month2.setYear(year);

        Month month3 = new Month();
        month3.setMonth(3);
        month3.setDuration(120);
        month3.setYear(year);

        year.getMonths().add(month1);
        year.getMonths().add(month2);
        year.getMonths().add(month3);
        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears()).hasSize(1);
        assertThat(response.getYears().get(0).getMonths()).hasSize(3);

        assertThat(response.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(1);
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(60);

        assertThat(response.getYears().get(0).getMonths().get(1).getMonth()).isEqualTo(2);
        assertThat(response.getYears().get(0).getMonths().get(1).getDuration()).isEqualTo(90);

        assertThat(response.getYears().get(0).getMonths().get(2).getMonth()).isEqualTo(3);
        assertThat(response.getYears().get(0).getMonths().get(2).getDuration()).isEqualTo(120);
    }

    @Test
    @DisplayName("Should map trainer with no years")
    void testToResponse_NoYears() {
        // Given - trainer with empty years list (from setUp)

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("john.doe");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getYears()).isEmpty();
    }

    @Test
    @DisplayName("Should map trainer with year but no months")
    void testToResponse_YearWithNoMonths() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());
        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears()).hasSize(1);
        assertThat(response.getYears().get(0).getYear()).isEqualTo(2025);
        assertThat(response.getYears().get(0).getMonths()).isEmpty();
    }

    @Test
    @DisplayName("Should map inactive trainer")
    void testToResponse_InactiveTrainer() {
        // Given
        trainer.setActive(false);

        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(5);
        month.setDuration(45);
        month.setYear(year);

        year.getMonths().add(month);
        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getIsActive()).isFalse();
        assertThat(response.getUsername()).isEqualTo("john.doe");
    }

    @Test
    @DisplayName("Should map trainer with zero duration months")
    void testToResponse_ZeroDurationMonths() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(6);
        month.setDuration(0);
        month.setYear(year);

        year.getMonths().add(month);
        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should map complex trainer with multiple years and months")
    void testToResponse_ComplexStructure() {
        // Given
        Year year2024 = new Year();
        year2024.setYear(2024);
        year2024.setTrainer(trainer);
        year2024.setMonths(new ArrayList<>());

        Month month2024_11 = new Month();
        month2024_11.setMonth(11);
        month2024_11.setDuration(100);
        month2024_11.setYear(year2024);

        Month month2024_12 = new Month();
        month2024_12.setMonth(12);
        month2024_12.setDuration(150);
        month2024_12.setYear(year2024);

        year2024.getMonths().add(month2024_11);
        year2024.getMonths().add(month2024_12);

        Year year2025 = new Year();
        year2025.setYear(2025);
        year2025.setTrainer(trainer);
        year2025.setMonths(new ArrayList<>());

        Month month2025_1 = new Month();
        month2025_1.setMonth(1);
        month2025_1.setDuration(200);
        month2025_1.setYear(year2025);

        Month month2025_2 = new Month();
        month2025_2.setMonth(2);
        month2025_2.setDuration(180);
        month2025_2.setYear(year2025);

        Month month2025_3 = new Month();
        month2025_3.setMonth(3);
        month2025_3.setDuration(220);
        month2025_3.setYear(year2025);

        year2025.getMonths().add(month2025_1);
        year2025.getMonths().add(month2025_2);
        year2025.getMonths().add(month2025_3);

        trainer.getYears().add(year2024);
        trainer.getYears().add(year2025);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("john.doe");
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Doe");
        assertThat(response.getIsActive()).isTrue();

        assertThat(response.getYears()).hasSize(2);

        // Verify 2024 data
        assertThat(response.getYears().get(0).getYear()).isEqualTo(2024);
        assertThat(response.getYears().get(0).getMonths()).hasSize(2);
        assertThat(response.getYears().get(0).getMonths().get(0).getMonth()).isEqualTo(11);
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(100);
        assertThat(response.getYears().get(0).getMonths().get(1).getMonth()).isEqualTo(12);
        assertThat(response.getYears().get(0).getMonths().get(1).getDuration()).isEqualTo(150);

        // Verify 2025 data
        assertThat(response.getYears().get(1).getYear()).isEqualTo(2025);
        assertThat(response.getYears().get(1).getMonths()).hasSize(3);
        assertThat(response.getYears().get(1).getMonths().get(0).getMonth()).isEqualTo(1);
        assertThat(response.getYears().get(1).getMonths().get(0).getDuration()).isEqualTo(200);
        assertThat(response.getYears().get(1).getMonths().get(1).getMonth()).isEqualTo(2);
        assertThat(response.getYears().get(1).getMonths().get(1).getDuration()).isEqualTo(180);
        assertThat(response.getYears().get(1).getMonths().get(2).getMonth()).isEqualTo(3);
        assertThat(response.getYears().get(1).getMonths().get(2).getDuration()).isEqualTo(220);
    }

    @Test
    @DisplayName("Should correctly map all trainer fields")
    void testToResponse_AllTrainerFields() {
        // Given
        trainer.setUsername("jane.smith");
        trainer.setFirstName("Jane");
        trainer.setLastName("Smith");
        trainer.setActive(false);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("jane.smith");
        assertThat(response.getFirstName()).isEqualTo("Jane");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should map trainer with large duration values")
    void testToResponse_LargeDurations() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        Month month = new Month();
        month.setMonth(1);
        month.setDuration(999999);
        month.setYear(year);

        year.getMonths().add(month);
        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears().get(0).getMonths().get(0).getDuration()).isEqualTo(999999);
    }

    @Test
    @DisplayName("Should map trainer with all 12 months")
    void testToResponse_All12Months() {
        // Given
        Year year = new Year();
        year.setYear(2025);
        year.setTrainer(trainer);
        year.setMonths(new ArrayList<>());

        for (int i = 1; i <= 12; i++) {
            Month month = new Month();
            month.setMonth(i);
            month.setDuration(i * 10);
            month.setYear(year);
            year.getMonths().add(month);
        }

        trainer.getYears().add(year);

        // When
        TrainingEventResponse response = EntityToDtoMapper.toResponse(trainer);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getYears().get(0).getMonths()).hasSize(12);

        for (int i = 0; i < 12; i++) {
            assertThat(response.getYears().get(0).getMonths().get(i).getMonth()).isEqualTo(i + 1);
            assertThat(response.getYears().get(0).getMonths().get(i).getDuration()).isEqualTo((i + 1) * 10);
        }
    }
}
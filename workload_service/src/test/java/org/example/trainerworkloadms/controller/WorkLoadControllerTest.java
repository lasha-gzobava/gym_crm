
package org.example.trainerworkloadms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.trainerworkloadms.dto.TrainingEventRequest;
import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(
        controllers = WorkLoadController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "org\\.example\\.trainerworkloadms\\.security\\..*")
        }
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WorkLoadController Tests")
class WorkLoadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkloadService workloadService;

    private TrainingEventRequest request;
    private TrainingEventResponse response;

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

        TrainingEventResponse.MonthlyWorkload monthlyWorkload =
                new TrainingEventResponse.MonthlyWorkload(3, 60);
        TrainingEventResponse.YearlyWorkload yearlyWorkload =
                new TrainingEventResponse.YearlyWorkload(2025, List.of(monthlyWorkload));

        response = new TrainingEventResponse();
        response.setUsername("john.doe");
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setIsActive(true);
        response.setYears(List.of(yearlyWorkload));
    }

    @Test
    @DisplayName("POST /workloads - Should accept training event and return response")
    void testAccept_Success() throws Exception {
        // Given
        when(workloadService.apply(any(TrainingEventRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.years").isArray())
                .andExpect(jsonPath("$.years[0].year").value(2025))
                .andExpect(jsonPath("$.years[0].months[0].month").value(3))
                .andExpect(jsonPath("$.years[0].months[0].duration").value(60));

        verify(workloadService, times(1)).apply(any(TrainingEventRequest.class));
    }

    @Test
    @DisplayName("POST /workloads - Should handle ADD action type")
    void testAccept_AddAction() throws Exception {
        // Given
        when(workloadService.apply(any(TrainingEventRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));

        verify(workloadService).apply(any(TrainingEventRequest.class));
    }

    @Test
    @DisplayName("POST /workloads - Should handle DELETE action type")
    void testAccept_DeleteAction() throws Exception {
        // Given
        request.setAction(TrainingEventRequest.ActionType.DELETE);
        TrainingEventResponse.MonthlyWorkload monthlyWorkload =
                new TrainingEventResponse.MonthlyWorkload(3, 40);
        TrainingEventResponse.YearlyWorkload yearlyWorkload =
                new TrainingEventResponse.YearlyWorkload(2025, List.of(monthlyWorkload));
        response.setYears(List.of(yearlyWorkload));

        when(workloadService.apply(any(TrainingEventRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years[0].months[0].duration").value(40));

        verify(workloadService).apply(any(TrainingEventRequest.class));
    }

    @Test
    @DisplayName("GET /workloads/summaries/{username} - Should return trainer summary when found")
    void testGetSummary_Found() throws Exception {
        // Given
        when(workloadService.getTrainer("john.doe")).thenReturn(Optional.of(response));

        // When & Then
        mockMvc.perform(get("/workloads/summaries/john.doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.years").isArray())
                .andExpect(jsonPath("$.years[0].year").value(2025))
                .andExpect(jsonPath("$.years[0].months[0].month").value(3))
                .andExpect(jsonPath("$.years[0].months[0].duration").value(60));

        verify(workloadService, times(1)).getTrainer("john.doe");
    }

    @Test
    @DisplayName("GET /workloads/summaries/{username} - Should return 404 when trainer not found")
    void testGetSummary_NotFound() throws Exception {
        // Given
        when(workloadService.getTrainer("unknown.user")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/workloads/summaries/unknown.user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(workloadService, times(1)).getTrainer("unknown.user");
    }

    @Test
    @DisplayName("GET /workloads/summaries/{username} - Should handle trainer with multiple years")
    void testGetSummary_MultipleYears() throws Exception {
        // Given
        TrainingEventResponse.MonthlyWorkload month1 = new TrainingEventResponse.MonthlyWorkload(3, 60);
        TrainingEventResponse.MonthlyWorkload month2 = new TrainingEventResponse.MonthlyWorkload(4, 90);
        TrainingEventResponse.YearlyWorkload year1 = new TrainingEventResponse.YearlyWorkload(2024, List.of(month1));
        TrainingEventResponse.YearlyWorkload year2 = new TrainingEventResponse.YearlyWorkload(2025, List.of(month2));

        response.setYears(List.of(year1, year2));

        when(workloadService.getTrainer("john.doe")).thenReturn(Optional.of(response));

        // When & Then
        mockMvc.perform(get("/workloads/summaries/john.doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years").isArray())
                .andExpect(jsonPath("$.years", hasSize(2)))
                .andExpect(jsonPath("$.years[0].year").value(2024))
                .andExpect(jsonPath("$.years[1].year").value(2025));

        verify(workloadService).getTrainer("john.doe");
    }

    @Test
    @DisplayName("GET /workloads/summaries/{username} - Should handle trainer with multiple months")
    void testGetSummary_MultipleMonths() throws Exception {
        // Given
        TrainingEventResponse.MonthlyWorkload month1 = new TrainingEventResponse.MonthlyWorkload(1, 100);
        TrainingEventResponse.MonthlyWorkload month2 = new TrainingEventResponse.MonthlyWorkload(2, 150);
        TrainingEventResponse.MonthlyWorkload month3 = new TrainingEventResponse.MonthlyWorkload(3, 60);
        TrainingEventResponse.YearlyWorkload yearlyWorkload =
                new TrainingEventResponse.YearlyWorkload(2025, List.of(month1, month2, month3));

        response.setYears(List.of(yearlyWorkload));

        when(workloadService.getTrainer("john.doe")).thenReturn(Optional.of(response));

        // When & Then
        mockMvc.perform(get("/workloads/summaries/john.doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years[0].months", hasSize(3)))
                .andExpect(jsonPath("$.years[0].months[0].month").value(1))
                .andExpect(jsonPath("$.years[0].months[0].duration").value(100))
                .andExpect(jsonPath("$.years[0].months[1].month").value(2))
                .andExpect(jsonPath("$.years[0].months[1].duration").value(150))
                .andExpect(jsonPath("$.years[0].months[2].month").value(3))
                .andExpect(jsonPath("$.years[0].months[2].duration").value(60));

        verify(workloadService).getTrainer("john.doe");
    }

    @Test
    @DisplayName("POST /workloads - Should handle request with inactive trainer")
    void testAccept_InactiveTrainer() throws Exception {
        // Given
        request.setIsActive(false);
        response.setIsActive(false);

        when(workloadService.apply(any(TrainingEventRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));

        verify(workloadService).apply(any(TrainingEventRequest.class));
    }

    @Test
    @DisplayName("GET /workloads/summaries/{username} - Should handle empty years list")
    void testGetSummary_EmptyYears() throws Exception {
        // Given
        response.setYears(new ArrayList<>());

        when(workloadService.getTrainer("john.doe")).thenReturn(Optional.of(response));

        // When & Then
        mockMvc.perform(get("/workloads/summaries/john.doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years").isArray())
                .andExpect(jsonPath("$.years", hasSize(0)));

        verify(workloadService).getTrainer("john.doe");
    }

    @Test
    @DisplayName("POST /workloads - Should process request with various durations")
    void testAccept_VariousDurations() throws Exception {
        // Given
        request.setDurationMinutes(120);
        TrainingEventResponse.MonthlyWorkload monthlyWorkload =
                new TrainingEventResponse.MonthlyWorkload(3, 120);
        TrainingEventResponse.YearlyWorkload yearlyWorkload =
                new TrainingEventResponse.YearlyWorkload(2025, List.of(monthlyWorkload));
        response.setYears(List.of(yearlyWorkload));

        when(workloadService.apply(any(TrainingEventRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/workloads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.years[0].months[0].duration").value(120));

        verify(workloadService).apply(any(TrainingEventRequest.class));
    }
}
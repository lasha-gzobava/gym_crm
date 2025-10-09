package org.example.trainerworkloadms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingEventResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private List<YearlyWorkload> years;

    @Data
    @AllArgsConstructor
    public static class YearlyWorkload {
        private int year;
        private List<MonthlyWorkload> months;
    }

    @Data
    @AllArgsConstructor
    public static class MonthlyWorkload {
        private int month;
        private int duration;
    }
}

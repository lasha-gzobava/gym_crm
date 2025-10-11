package org.example.dto.trainer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

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
    @NoArgsConstructor
    public static class YearlyWorkload {
        private int year;
        private List<MonthlyWorkload> months;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyWorkload {
        private int month;
        private int duration;
    }
}

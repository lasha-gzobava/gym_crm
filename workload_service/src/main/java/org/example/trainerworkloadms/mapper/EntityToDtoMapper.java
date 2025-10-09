package org.example.trainerworkloadms.mapper;

import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.entity.Trainer;

import java.util.List;

public class EntityToDtoMapper {

    public static TrainingEventResponse toResponse(Trainer trainer) {
        List<TrainingEventResponse.YearlyWorkload> years = trainer.getYears().stream()
                .map(y -> new TrainingEventResponse.YearlyWorkload(
                        y.getYear(),
                        y.getMonths().stream()
                                .map(m -> new TrainingEventResponse.MonthlyWorkload(
                                        m.getMonth(),
                                        m.getDuration()
                                ))
                                .toList()
                ))
                .toList();

        TrainingEventResponse resp = new TrainingEventResponse();
        resp.setUsername(trainer.getUsername());
        resp.setFirstName(trainer.getFirstName());
        resp.setLastName(trainer.getLastName());
        resp.setIsActive(trainer.isActive());
        resp.setYears(years);

        return resp;
    }
}

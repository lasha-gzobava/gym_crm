package org.example.trainerworkloadms.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.trainerworkloadms.dto.TrainingEventRequest;
import org.example.trainerworkloadms.dto.TrainingEventResponse;
import org.example.trainerworkloadms.entity.Month;
import org.example.trainerworkloadms.entity.Trainer;
import org.example.trainerworkloadms.entity.Year;
import org.example.trainerworkloadms.mapper.EntityToDtoMapper;
import org.example.trainerworkloadms.repository.TrainerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadService {

    private final TrainerRepository trainerRepository;

    @Transactional
    public TrainingEventResponse apply(TrainingEventRequest request){
        log.info("Processing training event request for trainer: {}", request.getUsername());
        log.debug("Request details: action={}, duration={}, date={}", 
                request.getAction(), request.getDurationMinutes(), request.getTrainingDate());
        
        //1 Find a trainer by username or create bew if not exists
        Trainer trainer = trainerRepository.findByUsername(request.getUsername())
                .orElseGet(() -> {
                    log.info("Trainer not found, creating new trainer: {}", request.getUsername());
                    Trainer newTrainer = new Trainer();
                    newTrainer.setUsername(request.getUsername());
                    newTrainer.setFirstName(request.getFirstName());
                    newTrainer.setLastName(request.getLastName());
                    newTrainer.setActive(request.getIsActive());
                    newTrainer.setYears(new ArrayList<>());
                    return newTrainer;
                });

        log.debug("Updating trainer information for: {}", request.getUsername());
        //Update trainer
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setActive(request.getIsActive());

        int yearVal = request.getTrainingDate().getYear();
        int monthVal = request.getTrainingDate().getMonthValue();

        int delta;
        if (request.getAction() == TrainingEventRequest.ActionType.ADD){
            delta = request.getDurationMinutes();
            log.debug("Adding {} minutes to workload for {}/{}", delta, yearVal, monthVal);
        }else {
            delta = -request.getDurationMinutes();
            log.debug("Removing {} minutes from workload for {}/{}", request.getDurationMinutes(), yearVal, monthVal);
        }

        //2 Find or create a year
        Year yearEntity = trainer.getYears().stream()
                .filter(y -> y.getYear() == yearVal)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Year {} not found, creating new year entry", yearVal);
                    Year newYear = new Year();
                    newYear.setYear(yearVal);
                    newYear.setTrainer(trainer);
                    newYear.setMonths(new ArrayList<>());
                    trainer.getYears().add(newYear);
                    return newYear;
                });

        //3 Find or create a month
        Month monthEntity = yearEntity.getMonths().stream()
                .filter(m -> m.getMonth() == monthVal)
                .findFirst()
                .orElseGet(() -> {
                    log.debug("Month {} not found in year {}, creating new month entry", monthVal, yearVal);
                    Month newMonth = new Month();
                    newMonth.setMonth(monthVal);
                    newMonth.setDuration(0);
                    newMonth.setYear(yearEntity);
                    yearEntity.getMonths().add(newMonth);
                    return newMonth;
                });

        //4 Update month duration
        int oldDuration = monthEntity.getDuration();
        int newDuration = monthEntity.getDuration() + delta;
        monthEntity.setDuration(Math.max(0, newDuration));
        log.info("Updated workload for trainer {} ({}/{}): {} -> {} minutes", 
                request.getUsername(), yearVal, monthVal, oldDuration, monthEntity.getDuration());

        //5 Save trainer
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Successfully saved trainer workload for: {}", request.getUsername());

        //6 Map entity to dto
        return EntityToDtoMapper.toResponse(savedTrainer);
    }

    public Optional<TrainingEventResponse> getTrainer(String username){
        log.info("Retrieving trainer workload for: {}", username);
        Optional<TrainingEventResponse> result = trainerRepository.findByUsername(username)
                .map(EntityToDtoMapper::toResponse);
        
        if (result.isEmpty()) {
            log.warn("Trainer not found: {}", username);
        } else {
            log.info("Successfully retrieved trainer workload for: {}", username);
        }
        
        return result;
    }
}

package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.trainingtype.TrainingTypeDto;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public List<TrainingTypeDto> getAll() {
        return trainingTypeRepository.findAll().stream()
                .map(t -> new TrainingTypeDto(t.getTrainingTypeId(), t.getTrainingTypeName()))
                .toList();
    }
}

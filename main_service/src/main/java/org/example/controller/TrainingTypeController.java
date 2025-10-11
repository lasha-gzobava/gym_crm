package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.trainingtype.TrainingTypeDto;
import org.example.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Training Type Management", description = "Endpoints for retrieving training types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @GetMapping
    @Operation(summary = "Retrieve all available training types")
    public ResponseEntity<List<TrainingTypeDto>> getAllTrainingTypes() {
        String tx = UUID.randomUUID().toString();
        log.info("[{}] Request received to fetch all training types", tx);

        List<TrainingTypeDto> types = trainingTypeService.getAll();

        log.info("[{}] Successfully retrieved {} training types", tx, types.size());
        return ResponseEntity.ok(types);
    }
}

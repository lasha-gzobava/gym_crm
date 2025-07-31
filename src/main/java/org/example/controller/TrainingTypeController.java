package org.example.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Training Type Management", description = "Endpoints for retrieving training types")
public class TrainingTypeController {
}

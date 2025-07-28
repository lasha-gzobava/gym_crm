package org.example.mapper;

import org.example.dto.TrainerDto;
import org.example.dto.UserDto;
import org.example.entity.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public TrainerDto toDto(Trainer trainer) {
        if (trainer == null || trainer.getUser() == null) return null;

        UserDto userDto = new UserDto();
        userDto.setId(trainer.getUser().getUserId());
        userDto.setFirstName(trainer.getUser().getFirstName());
        userDto.setLastName(trainer.getUser().getLastName());
        userDto.setUsername(trainer.getUser().getUsername());
        userDto.setIsActive(trainer.getUser().getIsActive());

        TrainerDto dto = new TrainerDto();
        dto.setId(trainer.getTrainerId());
        dto.setSpecialization(trainer.getSpecialization().getTrainingTypeName());
        dto.setUser(userDto);

        return dto;
    }
}

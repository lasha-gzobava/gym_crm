package org.example.mapper;


import org.example.dto.TraineeDto;
import org.example.dto.UserDto;
import org.example.entity.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public TraineeDto toDto(Trainee trainee) {
        if (trainee == null) return null;

        UserDto userDto = new UserDto();
        userDto.setId(trainee.getUser().getUserId());
        userDto.setFirstName(trainee.getUser().getFirstName());
        userDto.setLastName(trainee.getUser().getLastName());
        userDto.setUsername(trainee.getUser().getUsername());
        userDto.setIsActive(trainee.getUser().getIsActive());

        TraineeDto dto = new TraineeDto();
        dto.setId(trainee.getTraineeId());
        dto.setAddress(trainee.getAddress());
        dto.setDateOfBirth(trainee.getDateOfBirth());
        dto.setUser(userDto);
        return dto;
    }
}

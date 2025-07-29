package org.example.mapper;

import org.example.dto.TraineeDto;
import org.example.dto.UserDto;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private final TraineeMapper traineeMapper = new TraineeMapper();

    @Test
    void toDto_shouldMapAllFields() {
        // Prepare User entity
        User user = new User();
        user.setUserId(10L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setIsActive(true);

        // Prepare Trainee entity
        Trainee trainee = new Trainee();
        trainee.setTraineeId(5L);
        trainee.setAddress("123 Main St");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setUser(user);

        // Map to DTO
        TraineeDto dto = traineeMapper.toDto(trainee);

        // Verify mapping
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("123 Main St", dto.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDateOfBirth());

        UserDto userDto = dto.getUser();
        assertNotNull(userDto);
        assertEquals(10L, userDto.getId());
        assertEquals("John", userDto.getFirstName());
        assertEquals("Doe", userDto.getLastName());
        assertEquals("john.doe", userDto.getUsername());
        assertTrue(userDto.getIsActive());
    }

    @Test
    void toDto_shouldReturnNull_whenInputIsNull() {
        TraineeDto dto = traineeMapper.toDto(null);
        assertNull(dto);
    }
}

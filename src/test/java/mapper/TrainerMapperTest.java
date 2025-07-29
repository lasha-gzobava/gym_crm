package mapper;


import org.example.dto.TrainerDto;
import org.example.dto.UserDto;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.mapper.TrainerMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private final TrainerMapper trainerMapper = new TrainerMapper();

    @Test
    void toDto_shouldMapAllFields() {

        User user = new User();
        user.setUserId(20L);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUsername("alice.smith");
        user.setIsActive(false);


        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Crossfit");


        Trainer trainer = new Trainer();
        trainer.setTrainerId(7L);
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        TrainerDto dto = trainerMapper.toDto(trainer);


        assertNotNull(dto);
        assertEquals(7L, dto.getId());
        assertEquals("Crossfit", dto.getSpecialization());

        UserDto userDto = dto.getUser();
        assertNotNull(userDto);
        assertEquals(20L, userDto.getId());
        assertEquals("Alice", userDto.getFirstName());
        assertEquals("Smith", userDto.getLastName());
        assertEquals("alice.smith", userDto.getUsername());
        assertFalse(userDto.getIsActive());
    }

    @Test
    void toDto_shouldReturnNull_whenInputIsNull() {
        assertNull(trainerMapper.toDto(null));
    }

    @Test
    void toDto_shouldReturnNull_whenUserIsNull() {
        Trainer trainer = new Trainer();
        trainer.setUser(null);
        assertNull(trainerMapper.toDto(trainer));
    }
}

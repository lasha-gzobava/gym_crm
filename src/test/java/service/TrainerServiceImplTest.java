package service;


import org.example.dto.CreateTrainerDto;
import org.example.dto.PasswordChangeDto;
import org.example.dto.TrainerDto;
import org.example.dto.CreateUserDto;
import org.example.dto.UserDto;
import org.example.entity.Trainer;
import org.example.entity.TrainingType;
import org.example.entity.User;
import org.example.mapper.TrainerMapper;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.UserService;
import org.example.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_success() {
        CreateTrainerDto createDto = new CreateTrainerDto();
        createDto.setSpecialization("Yoga");

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setFirstName("Alice");
        createUserDto.setLastName("Smith");
        createDto.setUser(createUserDto);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");

        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setUsername("alice.smith");

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainingType);
        trainer.setUser(user);

        TrainerDto trainerDto = new TrainerDto();
        UserDto userDto = new UserDto();
        userDto.setFirstName("Alice");
        userDto.setLastName("Smith");
        trainerDto.setUser(userDto);

        when(trainingTypeRepository.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(trainingType));
        when(userService.createUser("Alice", "Smith")).thenReturn(user);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = trainerService.createTrainer(createDto);

        assertNotNull(result);
        assertEquals("Alice", result.getUser().getFirstName());
        verify(trainingTypeRepository).findByTrainingTypeName("Yoga");
        verify(userService).createUser("Alice", "Smith");
        verify(trainerRepository).save(any(Trainer.class));
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void getByUsername_found() {
        String username = "alice.smith";
        User user = new User();
        user.setUsername(username);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        TrainerDto trainerDto = new TrainerDto();
        UserDto userDto = new UserDto();
        userDto.setFirstName("Alice");
        userDto.setLastName("Smith");
        trainerDto.setUser(userDto);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = trainerService.getByUsername(username);

        assertNotNull(result);
        assertEquals("Alice", result.getUser().getFirstName());
        verify(trainerRepository).findByUsername(username);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void getByUsername_notFound_throws() {
        String username = "missing";
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainerService.getByUsername(username));
        assertEquals("Trainer not found", ex.getMessage());
        verify(trainerRepository).findByUsername(username);
        verifyNoMoreInteractions(trainerMapper);
    }

    @Test
    void changePassword_delegatesToUserService() {
        PasswordChangeDto dto = new PasswordChangeDto();
        dto.setUsername("trainer1");
        dto.setOldPassword("oldPass");
        dto.setNewPassword("newPass");

        trainerService.changePassword(dto);

        verify(userService).changePassword("trainer1", "oldPass", "newPass");
    }

    @Test
    void updateTrainer_success() {
        String username = "alice.smith";
        CreateTrainerDto dto = new CreateTrainerDto();
        dto.setSpecialization("Pilates");

        CreateUserDto userDto = new CreateUserDto();
        userDto.setFirstName("Alicia");
        userDto.setLastName("Smithson");
        dto.setUser(userDto);

        TrainingType pilatesType = new TrainingType();
        pilatesType.setTrainingTypeName("Pilates");

        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Smith");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(new TrainingType("Yoga"));

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByTrainingTypeName("Pilates")).thenReturn(Optional.of(pilatesType));

        trainerService.updateTrainer(username, dto);

        assertEquals("Alicia", trainer.getUser().getFirstName());
        assertEquals("Smithson", trainer.getUser().getLastName());
        assertEquals("Pilates", trainer.getSpecialization().getTrainingTypeName());

        verify(trainerRepository).findByUsername(username);
        verify(trainingTypeRepository).findByTrainingTypeName("Pilates");
        verify(trainerRepository).save(trainer);
    }

    @Test
    void toggleActive_delegatesToUserService() {
        String username = "alice.smith";
        trainerService.toggleActive(username);
        verify(userService).toggleActive(username);
    }

    @Test
    void deleteByUsername_success() {
        String username = "alice.smith";

        User user = new User();
        user.setUsername(username);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainer));

        trainerService.deleteByUsername(username);

        verify(trainerRepository).delete(trainer);
    }

    @Test
    void deleteByUsername_notFound_throws() {
        String username = "missing";
        when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trainerService.deleteByUsername(username));
        assertEquals("Trainer not found", ex.getMessage());
        verify(trainerRepository).findByUsername(username);
    }
}

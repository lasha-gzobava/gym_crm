package service;

import org.example.dto.*;
import org.example.entity.Trainee;
import org.example.entity.User;
import org.example.mapper.TraineeMapper;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainingRepository;
import org.example.service.UserService;
import org.example.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private UserService userService;

    @Mock
    private TrainingRepository trainingRepository;


    @InjectMocks
    private TraineeServiceImpl traineeService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_success() {
        // Prepare DTO and entities
        CreateTraineeDto createDto = new CreateTraineeDto();
        createDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createDto.setAddress("123 Main St");

        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setFirstName("John");
        createUserDto.setLastName("Doe");
        createDto.setUser(createUserDto);

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");

        Trainee trainee = new Trainee();
        trainee.setDateOfBirth(createDto.getDateOfBirth());
        trainee.setAddress(createDto.getAddress());
        trainee.setUser(user);

        // Create expected UserDto for the returned TraineeDto
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setUsername("john.doe");

        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setUser(userDto);

        // Mock behavior
        when(userService.createUser("John", "Doe")).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        // Call method
        TraineeDto result = traineeService.createTrainee(createDto);

        // Verify
        assertNotNull(result);
        assertEquals("John", result.getUser().getFirstName());
        verify(userService).createUser("John", "Doe");
        verify(traineeRepository).save(any(Trainee.class));
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void getByUsername_found() {
        String username = "john.doe";
        User user = new User();
        user.setUsername(username);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        TraineeDto traineeDto = new TraineeDto();
        UserDto userDto = new UserDto();  // Use UserDto here, not CreateUserDto
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        traineeDto.setUser(userDto);  // This matches TraineeDto#setUser(UserDto)

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = traineeService.getByUsername(username);

        assertNotNull(result);
        assertEquals("John", result.getUser().getFirstName());
        verify(traineeRepository).findByUsername(username);
        verify(traineeMapper).toDto(trainee);
    }


    @Test
    void getByUsername_notFound_throws() {
        String username = "missing";
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> traineeService.getByUsername(username));
        assertEquals("Trainee not found", ex.getMessage());
        verify(traineeRepository).findByUsername(username);
        verifyNoMoreInteractions(traineeMapper);
    }

    @Test
    void changePassword_delegatesToUserService() {
        PasswordChangeDto dto = new PasswordChangeDto();
        dto.setUsername("user1");
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        traineeService.changePassword(dto);

        verify(userService).changePassword("user1", "old", "new");
    }

    @Test
    void updateTrainee_success() {
        String username = "john.doe";
        CreateTraineeDto dto = new CreateTraineeDto();
        dto.setDateOfBirth(LocalDate.of(1991, 2, 2));
        dto.setAddress("New Address");
        CreateUserDto userDto = new CreateUserDto();
        userDto.setFirstName("Jane");
        userDto.setLastName("Smith");
        dto.setUser(userDto);

        User user = new User();
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        traineeService.updateTrainee(username, dto);

        assertEquals("Jane", trainee.getUser().getFirstName());
        assertEquals("Smith", trainee.getUser().getLastName());
        assertEquals("New Address", trainee.getAddress());
        assertEquals(dto.getDateOfBirth(), trainee.getDateOfBirth());

        verify(traineeRepository).findByUsername(username);
        verify(traineeRepository).save(trainee);
    }

    @Test
    void toggleActive_delegatesToUserService() {
        String username = "john.doe";
        traineeService.toggleActive(username);
        verify(userService).toggleActive(username);
    }

    @Test
    void deleteByUsername_success() {
        String username = "john.doe";

        User user = new User();
        user.setUsername(username);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        // Mock the training repository
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(trainee));

        // Add this if trainingRepository is involved in deletion
        doNothing().when(trainingRepository).deleteAllByTrainee(trainee);

        traineeService.deleteByUsername(username);

        verify(trainingRepository).deleteAllByTrainee(trainee); // verify cascade delete
        verify(traineeRepository).delete(trainee);
    }


    @Test
    void deleteByUsername_notFound_throws() {
        String username = "missing";
        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> traineeService.deleteByUsername(username));
        assertEquals("Trainee not found", ex.getMessage());
        verify(traineeRepository).findByUsername(username);
    }
}

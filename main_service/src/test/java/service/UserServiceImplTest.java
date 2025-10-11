package service;

import org.example.entity.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.UserRepository;
import org.example.service.impl.UserServiceImpl;
import org.example.util.UsernamePasswordGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainerRepository trainerRepository;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createUser_shouldCreateAndReturnUser() {
        // existing usernames -> empty
        when(userRepository.findAll()).thenReturn(List.of());
        // ensure username isn't flagged as taken by trainee/trainer
        when(traineeRepository.existsByUser_Username(anyString())).thenReturn(false);
        when(trainerRepository.existsByUser_Username(anyString())).thenReturn(false);
        // encode any raw password to a known value
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");
        // have save return the same user (typical Mockito pattern)
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.createUser("John", "Doe");

        assertNotNull(created);
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertNotNull(created.getUsername(), "username must be generated");
        assertEquals("encodedPwd", created.getPassword());

        verify(userRepository).findAll();
        verify(traineeRepository, atLeastOnce()).existsByUser_Username(anyString());
        verify(trainerRepository, atLeastOnce()).existsByUser_Username(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
        verifyNoMoreInteractions(userRepository, traineeRepository, trainerRepository, passwordEncoder);
    }

    @Test
    void getRawPassword_shouldReturnLastGeneratedPassword() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of());
        when(traineeRepository.existsByUser_Username(anyString())).thenReturn(false);
        when(trainerRepository.existsByUser_Username(anyString())).thenReturn(false);

        // Capture the random raw password that createUser() generates
        ArgumentCaptor<String> rawCaptor = ArgumentCaptor.forClass(String.class);
        when(passwordEncoder.encode(rawCaptor.capture())).thenReturn("enc");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        userService.createUser("Jane", "Doe");
        String lastRaw = userService.getRawPassword();

        // Assert
        assertNotNull(lastRaw, "last raw password must be stored");
        assertEquals(rawCaptor.getValue(), lastRaw, "stored raw password should match what was encoded");
    }

    @Test
    void changePassword_shouldUpdatePasswordIfOldMatches() {
        User user = new User("John", "Doe", "john.doe", "encodedOldPwd");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPwd", "encodedOldPwd")).thenReturn(true);
        when(passwordEncoder.encode("newPwd")).thenReturn("encodedNewPwd");

        userService.changePassword("john.doe", "oldPwd", "newPwd");

        assertEquals("encodedNewPwd", user.getPassword());
        verify(userRepository).save(user);
    }


    @Test
    void changePassword_wrongOldPassword_throwsIllegalArgumentException() {
        // arrange
        String username = "john.doe";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encoded-old");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        // oldPassword does NOT match stored hash
        when(passwordEncoder.matches("WRONG_OLD", "encoded-old")).thenReturn(false);

        // act + assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword(username, "WRONG_OLD", "newOne!"));
    }

    @Test
    void changePassword_shouldThrowIfOldPasswordWrong() {
        User user = new User("John", "Doe", "john.doe", "encodedOldPwd");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPwd", "encodedOldPwd")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.changePassword("john.doe", "wrongPwd", "newPwd"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void setActiveStatus_shouldUpdateStatus() {
        User user = new User("John", "Doe", "john.doe", "pwd");
        user.setIsActive(false);

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        userService.setActiveStatus("john.doe", true);

        assertTrue(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void getByUsername_shouldReturnUserIfFound() {
        User user = new User("John", "Doe", "john.doe", "pwd");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("john.doe");

        assertEquals(user, result);
    }

    @Test
    void getByUsername_shouldThrowIfNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getByUsername("unknown"));
    }

    @Test
    void authenticate_shouldReturnUserIfCredentialsMatch() {
        User user = new User("John", "Doe", "john.doe", "encodedPwd");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPwd", "encodedPwd")).thenReturn(true);

        User result = userService.authenticate("john.doe", "rawPwd");

        assertEquals(user, result);
    }

    @Test
    void authenticate_shouldThrowIfPasswordWrong() {
        User user = new User("John", "Doe", "john.doe", "encodedPwd");

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPwd", "encodedPwd")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.authenticate("john.doe", "wrongPwd"));
    }

    @Test
    void authenticate_shouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.authenticate("missing", "pwd"));
    }
}

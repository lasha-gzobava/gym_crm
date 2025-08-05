package service;

import org.example.entity.User;
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldCreateAndReturnUser() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");

        User created = userService.createUser("John", "Doe");

        assertNotNull(created);
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertNotNull(created.getUsername());
        assertEquals("encodedPwd", created.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void getRawPassword_shouldReturnLastGeneratedPassword() {
        userService.createUser("Jane", "Doe");
        String pwd = userService.getRawPassword();
        assertNotNull(pwd);
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

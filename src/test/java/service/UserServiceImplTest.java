package service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
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

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldGenerateAndSaveUser() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");

        User result = userService.createUser("John", "Doe");

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertNotNull(result.getUsername());
        assertEquals("encodedPass", result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_shouldUpdatePasswordIfOldMatches() {
        User user = new User("John", "Doe", "jdoe", "oldEncoded");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");

        userService.changePassword("jdoe", "old", "new");

        assertEquals("newEncoded", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowIfOldDoesNotMatch() {
        User user = new User("John", "Doe", "jdoe", "oldEncoded");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOld", "oldEncoded")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword("jdoe", "wrongOld", "new")
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void toggleActive_shouldFlipStatus() {
        User user = new User("John", "Doe", "jdoe", "pass");
        user.setIsActive(true);

        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));

        userService.toggleActive("jdoe");

        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void getByUsername_shouldReturnUser() {
        User user = new User("John", "Doe", "jdoe", "pass");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("jdoe");

        assertEquals("jdoe", result.getUsername());
    }

    @Test
    void getByUsername_shouldThrowIfNotFound() {
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.getByUsername("jdoe")
        );
    }

    @Test
    void authenticate_shouldReturnUserIfPasswordMatches() {
        User user = new User("John", "Doe", "jdoe", "encodedPassword");

        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encodedPassword")).thenReturn(true); // Mock this!

        User result = userService.authenticate("jdoe", "pass");

        assertEquals(user, result);
    }


    @Test
    void authenticate_shouldThrowIfUserNotFound() {
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.authenticate("jdoe", "pass")
        );
    }

    @Test
    void authenticate_shouldThrowIfPasswordWrong() {
        User user = new User("John", "Doe", "jdoe", "correct");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
                userService.authenticate("jdoe", "wrong")
        );
    }
}

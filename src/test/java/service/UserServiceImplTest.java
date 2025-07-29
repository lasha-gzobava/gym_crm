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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_success() {
        String firstName = "John";
        String lastName = "Doe";

        List<String> existingUsernames = List.of("john.doe");

        when(userRepository.findAll()).thenReturn(List.of(new User(firstName, lastName, "john.doe", "encodedPass")));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedRandomPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User createdUser = userService.createUser(firstName, lastName);

        assertNotNull(createdUser);
        assertEquals(firstName, createdUser.getFirstName());
        assertEquals(lastName, createdUser.getLastName());
        assertNotNull(createdUser.getUsername());
        assertEquals("encodedRandomPassword", createdUser.getPassword());

        verify(userRepository).findAll();
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_success() {
        String username = "john.doe";
        String oldPassword = "oldPass";
        String newPassword = "newPass";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedOldPass");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");
        when(userRepository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> userService.changePassword(username, oldPassword, newPassword));
        assertEquals("encodedNewPass", user.getPassword());

        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(oldPassword, "encodedOldPass");
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_wrongOldPassword_throws() {
        String username = "john.doe";
        String oldPassword = "wrongOld";
        String newPassword = "newPass";

        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedOldPass");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(oldPassword, "encodedOldPass")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.changePassword(username, oldPassword, newPassword)
        );

        assertEquals("Old password does not match.", exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(oldPassword, "encodedOldPass");
        verify(userRepository, never()).save(any());
    }

    @Test
    void toggleActive_togglesStatus() {
        String username = "john.doe";

        User user = new User();
        user.setUsername(username);
        user.setIsActive(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.toggleActive(username);

        assertFalse(user.getIsActive());
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void getByUsername_found() {
        String username = "john.doe";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User foundUser = userService.getByUsername(username);

        assertNotNull(foundUser);
        assertEquals(username, foundUser.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getByUsername_notFound_throws() {
        String username = "missing";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.getByUsername(username)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername(username);
    }
}

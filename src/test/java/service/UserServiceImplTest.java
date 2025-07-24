package service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;
    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        userDao = mock(UserDao.class);
        passwordEncoder = mock(PasswordEncoder.class);

        userService.setUserDao(userDao);
        userService.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void testCreateUser_firstUsername() {
        when(userDao.getAll()).thenReturn(List.of());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        User user = userService.createUser("Alice", "Smith");

        assertNotNull(user);
        assertEquals("Alice.Smith", user.getUsername());
        assertEquals("encodedPass", user.getPassword());
        verify(userDao).save(any(User.class));
    }

    @Test
    void testCreateUser_duplicateUsername() {
        User existing = new User(1L, "Alice", "Smith", "Alice.Smith", "pass", true);
        when(userDao.getAll()).thenReturn(List.of(existing));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");

        User user = userService.createUser("Alice", "Smith");

        assertTrue(user.getUsername().startsWith("Alice.Smith"));
        assertNotEquals("Alice.Smith", user.getUsername()); // Should be suffixed
        verify(userDao).save(any(User.class));
    }

    @Test
    void testUpdateUser_success() {
        User user = new User(1L, "Bob", "Brown", "bob.brown", "pass", true);
        when(userDao.update(user)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.updateUser(user));
        verify(userDao).update(user);
    }

    @Test
    void testUpdateUser_notFound() {
        User user = new User(2L, "Charlie", "Chaplin", "charlie.chaplin", "pass", true);
        when(userDao.update(user)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.updateUser(user));
    }

    @Test
    void testDeactivateUser_success() {
        User user = new User(3L, "Diana", "Doe", "diana.doe", "pass", true);
        when(userDao.getById(3L)).thenReturn(user);
        when(userDao.update(any())).thenReturn(Optional.of(user));

        userService.deactivateUser(3L);

        assertFalse(user.isActive());
        verify(userDao).update(user);
    }

    @Test
    void testDeactivateUser_notFound() {
        when(userDao.getById(99L)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> userService.deactivateUser(99L));
    }

    @Test
    void testGetUserById() {
        User user = new User(4L, "Eve", "Evans", "eve.evans", "pass", true);
        when(userDao.getById(4L)).thenReturn(user);

        User result = userService.getUserById(4L);
        assertEquals("Eve", result.getFirstName());
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(
                new User(1L, "A", "B", "a.b", "x", true),
                new User(2L, "C", "D", "c.d", "y", false)
        );
        when(userDao.getAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
    }
}

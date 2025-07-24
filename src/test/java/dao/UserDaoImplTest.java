
package dao;

import org.example.dao.impl.UserDaoImpl;
import org.example.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoImplTest {

    private UserDaoImpl userDao;
    private Map<Long, User> userStorage;

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl();
        userStorage = new HashMap<>();
        userDao.setUserStorage(userStorage);
    }

    @Test
    void testSaveAndGetById() {
        User user = new User(1L, "John", "Doe", "johndoe", "pass", true);
        userDao.save(user);
        assertEquals(user, userDao.getById(1L));
    }

    @Test
    void testSaveDuplicateThrowsException() {
        User user1 = new User(1L, "Alice", "Smith", "alices", "pass", true);
        userDao.save(user1);
        User user2 = new User(1L, "Bob", "Jones", "bobj", "pass", true);
        assertThrows(IllegalArgumentException.class, () -> userDao.save(user2));
    }

    @Test
    void testUpdateExistingUser() {
        User user = new User(1L, "Jane", "Doe", "janedoe", "pass", true);
        userDao.save(user);
        user.setFirstName("Janet");
        Optional<User> updated = userDao.update(user);
        assertTrue(updated.isPresent());
        assertEquals("Janet", userDao.getById(1L).getFirstName());
    }

    @Test
    void testUpdateNonExistentUser() {
        User user = new User(2L, "Non", "Existent", "nonexist", "pass", false);
        assertTrue(userDao.update(user).isEmpty());
    }

    @Test
    void testDeleteUser() {
        User user = new User(3L, "Delete", "Me", "delme", "pass", true);
        userDao.save(user);
        userDao.delete(3L);
        assertNull(userDao.getById(3L));
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User(1L, "A", "One", "a1", "pass", true);
        User user2 = new User(2L, "B", "Two", "b2", "pass", false);
        userDao.save(user1);
        userDao.save(user2);
        List<User> all = userDao.getAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(user1));
        assertTrue(all.contains(user2));
    }
}

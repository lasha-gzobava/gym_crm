package org.example.service.impl;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private PasswordEncoder passwordEncoder;
    private UserDao userDao;
    private final AtomicLong idGenerator = new AtomicLong();


    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }



    @Override
    public User createUser(String firstName, String lastName) {
        logger.info("Creating user: {} {}", firstName, lastName);

        Long id = idGenerator.incrementAndGet();
        String baseUsername = firstName + "." + lastName;
        String username = generateUsername(baseUsername);

        String rawPassword = generateRandomPassword(10);
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(id, firstName, lastName, username, encodedPassword, true);
        userDao.save(user);

        logger.info("User created: ID={}, Username={}, RawPassword={}", id, username, rawPassword);
        return user;
    }

    private String generateUsername(String base) {
        long count = userDao.getAll().stream()
                .filter(u -> u.getUsername().startsWith(base))
                .count();

        return (count == 0) ? base : base + (count + 1);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    public void updateUser(User user) {
        Optional<User> updated = userDao.update(user);
        if (updated.isEmpty()) {
            throw new NoSuchElementException("User not found with ID: " + user.getId());
        }
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userDao.getById(id);
        if (user == null) {
            throw new NoSuchElementException("User not found with ID: " + id);
        }
        user.setActive(false);
        userDao.update(user);
        logger.info("User deactivated: ID={}", id);
    }

    @Override
    public User getUserById(Long id) {
        return userDao.getById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAll();
    }
}

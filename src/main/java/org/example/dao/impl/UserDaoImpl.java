package org.example.dao.impl;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
    private Map<Long, User> userStorage;

    @Autowired
    public void setUserStorage(Map<Long, User> userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User save(User user) {
        if (userStorage.containsKey(user.getId())) {
            throw new IllegalArgumentException("User already exists with ID: " + user.getId());
        }
        userStorage.put(user.getId(), user);
        logger.info("User saved: ID={}", user.getId());
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        if (!userStorage.containsKey(user.getId())) return Optional.empty();
        userStorage.put(user.getId(), user);
        logger.info("User updated: ID={}", user.getId());
        return Optional.of(user);
    }

    @Override
    public void delete(Long id) {
        userStorage.remove(id);
        logger.info("User deleted: ID={}", id);
    }

    @Override
    public User getById(Long id) {
        return userStorage.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }
}

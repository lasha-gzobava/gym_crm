package org.example.dao;

import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> update(User user);
    void delete(Long id);
    User getById(Long id);
    List<User> getAll();
}

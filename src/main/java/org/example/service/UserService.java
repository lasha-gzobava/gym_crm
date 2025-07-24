package org.example.service;

import org.example.entity.User;

import java.util.List;

public interface UserService {
    User createUser(String firstName, String lastName);
    void updateUser(User user);
    void deactivateUser(Long id);
    User getUserById(Long id);
    List<User> getAllUsers();
}

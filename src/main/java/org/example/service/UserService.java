package org.example.service;

import org.example.entity.User;

public interface UserService {
    User createUser(String firstName, String lastName);
    void changePassword(String username, String oldPassword, String newPassword);
    void toggleActive(String username);
    User getByUsername(String username);
    User authenticate(String username, String password);
}

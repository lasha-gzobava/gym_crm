package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.UsernamePasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(String firstName, String lastName) {
        List<String> existing = userRepository.findAll()
                .stream().map(User::getUsername).toList();

        String username = UsernamePasswordGenerator.generateUniqueUsername(firstName, lastName, existing);
        String rawPassword = UsernamePasswordGenerator.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = new User(firstName, lastName, username, encodedPassword);
        userRepository.save(user);
        log.info("Created user: {}", username);
        return user;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = getByUsername(username);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Incorrect old password for: {}", username);
            throw new IllegalArgumentException("Old password does not match.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", username);
    }

    @Override
    public void toggleActive(String username) {
        User user = getByUsername(username);
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
        log.info("Toggled active status: {} → {}", username, user.getIsActive());
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

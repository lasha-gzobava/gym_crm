package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.UsernamePasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private String lastRawPassword;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public User createUser(String firstName, String lastName) {
        List<String> existingUsernames = userRepository.findAll()
                .stream().map(User::getUsername).toList();

        String username;
        do {
            username = UsernamePasswordGenerator.generateUniqueUsername(firstName, lastName, existingUsernames);
        } while (traineeRepository.existsByUsername(username) || trainerRepository.existsByUsername(username));

        String rawPassword = UsernamePasswordGenerator.generateRandomPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        this.lastRawPassword = rawPassword;

        User user = new User(firstName, lastName, username, encodedPassword);
        userRepository.save(user);

        try (PrintWriter writer = new PrintWriter(new FileWriter("generated-credentials.txt", true))) {
            writer.println("Username: " + username + " | Password: " + rawPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Created user: {} with password: {}", username, rawPassword);
        return user;
    }


    @Override
    public String getRawPassword() {
        return lastRawPassword;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Received request to change password for: {}", username);

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
    public void setActiveStatus(String username, boolean isActive) {
        User user = getByUsername(username);
        user.setIsActive(isActive);
        userRepository.save(user);
        log.info("Set active status of {} to {}", username, isActive);
    }


    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }


}

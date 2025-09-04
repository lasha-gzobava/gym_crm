package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthStatusController {
    private final BruteForceProtectionService bruteForce;

    @GetMapping("/status")
    public Map<String, Object> status(@RequestParam String username) {
        return Map.of(
                "blocked", bruteForce.isBlocked(username),
                "secondsUntilUnlock", bruteForce.secondsUntilUnlock(username)
        );
    }
}
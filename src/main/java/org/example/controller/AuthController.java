package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.login.PasswordChangeDto;
import org.example.security.JwtService;
import org.example.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Login to get JWT and change password")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService; // for password change

    // ---------- LOGIN (JWT) ----------
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login with username/password and get a JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest body) {
        log.info("Login attempt for user: {}", body.getUsername());

        // Triggers your LockingAuthenticationProvider + brute-force checks
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword())
        );

        // Put any extra claims you want in the token (e.g., roles)
        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = jwtService.generateToken(auth.getName(), Map.of("roles", roles));

        log.info("Login successful for user: {}", auth.getName());
        return ResponseEntity.ok(new LoginResponse("Bearer", token, auth.getName(), roles));
    }

    // ---------- CHANGE PASSWORD ----------
    @PutMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Change password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto dto) {
        log.info("Password change requested for user: {}", dto.getUsername());
        userService.changePassword(dto.getUsername(), dto.getOldPassword(), dto.getNewPassword());
        log.info("Password changed for user: {}", dto.getUsername());
        return ResponseEntity.ok("Password changed successfully");
    }


    // ===== DTOs =====
    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class LoginResponse {
        private String tokenType;      // e.g. "Bearer"
        private String token;          // null in /me
        private String username;
        private List<String> roles;
    }
}

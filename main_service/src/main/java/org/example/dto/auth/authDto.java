package org.example.dto.auth;

public class authDto {
    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token, long expiresInMs) {}
}

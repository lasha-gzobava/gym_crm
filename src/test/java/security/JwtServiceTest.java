package security;


import io.jsonwebtoken.ExpiredJwtException;
import org.example.security.JwtService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET_32B = "01234567890123456789012345678901"; // 32 bytes for HS256

    @Test
    void generate_extract_validate_roundtrip() {
        JwtService jwt = new JwtService(SECRET_32B, 60_000); // 60s

        String token = jwt.generateToken("alice", Map.of("role", "USER"));
        assertNotNull(token);

        String username = jwt.extractUsername(token);
        assertEquals("alice", username);

        assertTrue(jwt.isTokenValid(token, "alice"));
        assertFalse(jwt.isTokenValid(token, "bob"));
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtService jwt = new JwtService(SECRET_32B, 1); // 1 ms
        String token = jwt.generateToken("eve", null);

        // wait a tiny bit to ensure it's expired
        try { Thread.sleep(3); } catch (InterruptedException ignored) {}

        assertThrows(ExpiredJwtException.class, () -> jwt.isTokenValid(token, "eve"));
    }
}

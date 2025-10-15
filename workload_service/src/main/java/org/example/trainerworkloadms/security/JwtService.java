package org.example.trainerworkloadms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Reads a secret key from application.properties and creates a secret key object
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Parses the token and returns the claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Extracts the username from the token
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            log.warn(" Failed to extract username: {}", e.getMessage());
            return null;
        }
    }


    // Validates the token
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            log.warn(" Invalid or expired token: {}", e.getMessage());
            return false;
        }
    }

    // Checks if the system issues the token
    public boolean isSystemToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String subject = claims.getSubject();
            Date exp = claims.getExpiration();

            boolean valid = "gym-crm-service".equals(subject) && exp.after(new Date());
            if (valid) {
                log.debug(" Valid system token from gym-crm-service, expires at {}", exp);
            } else {
                log.debug(" Not a system token: subject={}, exp={}", subject, exp);
            }
            return valid;
        } catch (Exception e) {
            log.debug(" Failed to parse system token: {}", e.getMessage());
            return false;
        }
    }
}

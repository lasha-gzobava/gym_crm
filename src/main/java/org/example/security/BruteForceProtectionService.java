package org.example.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BruteForceProtectionService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_SECONDS = 5 * 60; //5 min


    private static final class Attempt {
        int count = 0;
        Instant lockUntil = null;
    }

    private final Map<String, Attempt> state = new ConcurrentHashMap<>();

    public void registerLoginFailure(String username) {
        if (username == null) return;
        Attempt a = state.computeIfAbsent(username.toLowerCase(), k -> new Attempt());
        if (a.lockUntil != null && Instant.now().isBefore(a.lockUntil)) {
            return;
        }
        a.count++;
        if (a.count >= MAX_ATTEMPTS) {
            a.lockUntil = Instant.now().plusSeconds(LOCK_SECONDS);
        }
    }

    public void registerLoginSuccess(String username) {
        if (username == null) return;
        state.remove(username.toLowerCase());
    }

    public boolean isBlocked(String username) {
        if (username == null) return false;
        Attempt a = state.get(username.toLowerCase());
        if (a == null || a.lockUntil == null) return false;
        if (Instant.now().isBefore(a.lockUntil)) return true;
        state.remove(username.toLowerCase());
        return false;
    }

    public long secondsUntilUnlock(String username) {
        if (username == null) return 0;
        Attempt a = state.get(username.toLowerCase());
        if (a == null || a.lockUntil == null) return 0;
        long sec = a.lockUntil.getEpochSecond() - Instant.now().getEpochSecond();
        return Math.max(sec, 0);
    }
}
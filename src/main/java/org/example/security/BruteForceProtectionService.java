package org.example.security;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BruteForceProtectionService {
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_SECONDS = 5 * 60; //5 min


    @Slf4j
    private static final class Attempt {
        int count = 0;
        Instant lockUntil = null;
    }

    private final Map<String, Attempt> state = new ConcurrentHashMap<>();

    public void registerLoginFailure(String username) {
        if (username == null) return;
        final String u = username.toLowerCase();
        Attempt a = state.computeIfAbsent(username.toLowerCase(), k -> new Attempt());
        if (a.lockUntil != null && Instant.now().isBefore(a.lockUntil)) {
            log.debug("[BF] failure for '{}' but already locked until {}", u, a.lockUntil);
            return;
        }
        a.count++;
        log.debug("[BF] failure for '{}': count={}/{}", u, a.count, MAX_ATTEMPTS);
        if (a.count >= MAX_ATTEMPTS) {
            a.lockUntil = Instant.now().plusSeconds(LOCK_SECONDS);
            log.debug("[BF] '{}' LOCKED until {}", u, a.lockUntil);
        }
    }

    public void registerLoginSuccess(String username) {
        if (username == null) return;
        final String u = username.toLowerCase();
        if (state.remove(u) != null) {
            log.debug("[BF] success for '{}': counters reset", u);
        } else {
            log.debug("[BF] success for '{}': no prior failures", u);
        }
    }

    public boolean isBlocked(String username) {
        if (username == null) return false;
        final String u = username.toLowerCase();
        Attempt a = state.get(username.toLowerCase());
        if (a == null || a.lockUntil == null) return false;
        if (Instant.now().isBefore(a.lockUntil)){
            log.debug("[BF] '{}' is currently LOCKED until {}", u, a.lockUntil);
            return true;
        }
        state.remove(u);
        log.debug("[BF] '{}' lock expired — cleaned up", u);
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
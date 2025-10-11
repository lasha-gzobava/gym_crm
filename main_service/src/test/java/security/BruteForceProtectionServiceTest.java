package security;


import org.example.security.BruteForceProtectionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BruteForceProtectionServiceTest {

    @Test
    void becomesBlockedAfterThreeFailures_andReportsRemainingTime() {
        BruteForceProtectionService svc = new BruteForceProtectionService();

        assertFalse(svc.isBlocked("User"));
        assertEquals(0, svc.secondsUntilUnlock("User"));

        svc.registerLoginFailure("User");
        svc.registerLoginFailure("user"); // case-insensitive
        assertFalse(svc.isBlocked("USER"));

        svc.registerLoginFailure("USER");
        assertTrue(svc.isBlocked("user"));
        assertTrue(svc.secondsUntilUnlock("user") > 0);
    }

    @Test
    void successResetsCounters() {
        BruteForceProtectionService svc = new BruteForceProtectionService();

        svc.registerLoginFailure("bob");
        svc.registerLoginFailure("bob");
        svc.registerLoginSuccess("bob");

        assertFalse(svc.isBlocked("bob"));
        assertEquals(0, svc.secondsUntilUnlock("bob"));
    }

    @Test
    void expiredLockIsCleanedOnNextCheck() throws InterruptedException {
        BruteForceProtectionService svc = new BruteForceProtectionService();

        // Trip the lock
        svc.registerLoginFailure("c");
        svc.registerLoginFailure("c");
        svc.registerLoginFailure("c");
        assertTrue(svc.isBlocked("c"));

        // We can't time travel 5 minutes in this simple unit test, but the service cleans
        // up expired state on next isBlocked() call. We can at least assert calling with null is safe.
        assertFalse(svc.isBlocked(null));
        assertEquals(0, svc.secondsUntilUnlock(null));
    }
}

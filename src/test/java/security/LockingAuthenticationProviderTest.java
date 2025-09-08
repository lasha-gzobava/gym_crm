package security;



import org.example.security.BruteForceProtectionService;
import org.example.security.LockingAuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LockingAuthenticationProviderTest {

    @Test
    void blocksWhenBruteForceSaysBlocked() {
        UserDetailsService uds = mock(UserDetailsService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        BruteForceProtectionService brute = mock(BruteForceProtectionService.class);

        when(brute.isBlocked("alice")).thenReturn(true);

        LockingAuthenticationProvider provider = new LockingAuthenticationProvider(uds, encoder, brute);

        var token = UsernamePasswordAuthenticationToken.unauthenticated("alice", "pw");
        assertThrows(LockedException.class, () -> provider.authenticate(token));

        verify(brute, never()).registerLoginSuccess(any());
        verify(brute, never()).registerLoginFailure(any());
    }

    @Test
    void onSuccess_clearsBruteForceCounters() {
        UserDetailsService uds = mock(UserDetailsService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        BruteForceProtectionService brute = mock(BruteForceProtectionService.class);

        when(brute.isBlocked("bob")).thenReturn(false);
        when(uds.loadUserByUsername("bob")).thenReturn(
                new User("bob", "ENC", List.of())
        );
        when(encoder.matches("pw", "ENC")).thenReturn(true);

        LockingAuthenticationProvider provider = new LockingAuthenticationProvider(uds, encoder, brute);

        var token = UsernamePasswordAuthenticationToken.unauthenticated("bob", "pw");
        var auth = provider.authenticate(token);

        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());

        verify(brute).registerLoginSuccess("bob");
        verify(brute, never()).registerLoginFailure(any());
    }

    @Test
    void onBadCredentials_registersFailureAndPropagates() {
        UserDetailsService uds = mock(UserDetailsService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        BruteForceProtectionService brute = mock(BruteForceProtectionService.class);

        when(brute.isBlocked("sam")).thenReturn(false);
        when(uds.loadUserByUsername("sam")).thenReturn(
                new User("sam", "ENC", List.of())
        );
        when(encoder.matches("wrong", "ENC")).thenReturn(false);

        LockingAuthenticationProvider provider = new LockingAuthenticationProvider(uds, encoder, brute);

        var token = UsernamePasswordAuthenticationToken.unauthenticated("sam", "wrong");

        assertThrows(BadCredentialsException.class, () -> provider.authenticate(token));

        verify(brute).registerLoginFailure("sam");
        verify(brute, never()).registerLoginSuccess(any());
    }

    @Test
    void nonBadCredentialsRuntimeExceptionAlsoRegistersFailure() {
        UserDetailsService uds = mock(UserDetailsService.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        BruteForceProtectionService brute = mock(BruteForceProtectionService.class);

        when(brute.isBlocked("tom")).thenReturn(false);
        when(uds.loadUserByUsername("tom")).thenThrow(new RuntimeException("DB down"));

        LockingAuthenticationProvider provider = new LockingAuthenticationProvider(uds, encoder, brute);

        var token = UsernamePasswordAuthenticationToken.unauthenticated("tom", "pw");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> provider.authenticate(token));
        assertEquals("DB down", ex.getMessage());

        verify(brute).registerLoginFailure("tom");
        verify(brute, never()).registerLoginSuccess(any());
    }
}

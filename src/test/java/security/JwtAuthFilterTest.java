package security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.security.JwtAuthFilter;
import org.example.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthFilterTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void publicEndpoints_areNotFiltered() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService uds = mock(UserDetailsService.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getRequestURI()).thenReturn("/auth/login");  // public endpoint
        when(req.getMethod()).thenReturn("GET");

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void missingAuthHeader_passesThroughWithoutAuth() throws IOException, ServletException {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService uds = mock(UserDetailsService.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getRequestURI()).thenReturn("/api/data");
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validToken_setsAuthentication() throws IOException, ServletException {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService uds = mock(UserDetailsService.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getRequestURI()).thenReturn("/api/secure");
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer token123");
        when(jwtService.extractUsername("token123")).thenReturn("alice");
        when(jwtService.isTokenValid(eq("token123"), eq("alice"))).thenReturn(true);
        when(uds.loadUserByUsername("alice"))
                .thenReturn(new User("alice", "x", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        filter.doFilter(req, res, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
        assertEquals("alice", ((User) auth.getPrincipal()).getUsername());
        assertTrue(auth.isAuthenticated());

        verify(chain).doFilter(req, res);
    }

    @Test
    void invalidToken_leavesContextUnauthenticated() throws IOException, ServletException {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService uds = mock(UserDetailsService.class);
        JwtAuthFilter filter = new JwtAuthFilter(jwtService, uds);

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(req.getRequestURI()).thenReturn("/api/secure");
        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer bad");
        when(jwtService.extractUsername("bad")).thenThrow(new RuntimeException("invalid"));

        filter.doFilter(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }
}

package org.example.trainerworkloadms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    //  Filter incoming requests for JWT tokens and validate them
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            //  Validate token once
            boolean valid = jwtService.isTokenValid(token);

            if (!valid) {
                log.warn(" Invalid or expired JWT token");
                chain.doFilter(request, response);  // continue filter chain
                return;
            }

            //  Handle system-issued tokens (no username needed)
            if ("gym-crm-service".equalsIgnoreCase(jwtService.extractUsername(token))) {
                log.info(" Authorized system token from gym-crm-service");
                var auth = new UsernamePasswordAuthenticationToken(
                        "gym-crm-service", null, Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                chain.doFilter(request, response);
                return;
            }

            //  Normal user token
            String username = jwtService.extractUsername(token);
            var auth = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            chain.doFilter(request, response);

        } catch (Exception e) {
            log.error(" JWT validation error: {}", e.getMessage());
            chain.doFilter(request, response); // still continue chain
        }
    }
}

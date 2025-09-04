package org.example.config;

import lombok.RequiredArgsConstructor;
import org.example.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;                 // your OncePerRequestFilter
    private final AuthenticationProvider authenticationProvider; // your LockingAuthenticationProvider bean

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // We use JWT, so no CSRF sessions
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // Public auth & registration endpoints
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/trainee/register", "/trainer/register").permitAll()
                        // Dev tools
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Allow CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Everything else requires JWT
                        .anyRequest().authenticated()
                )

                // No default login mechanisms in JWT setups
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // Nice JSON 401 for missing/invalid token
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, resp, e) -> {
                    resp.setStatus(401);
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"error\":\"Unauthorized\"}");
                }))

                // H2 console needs frames
                .headers(h -> h.frameOptions(f -> f.disable()))

                // Our auth provider (with brute-force protection) + JWT filter
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // a simple logout endpoint
        http.logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler((req, resp, auth) -> {
                    resp.setStatus(200);
                    resp.setContentType("application/json");
                    resp.getWriter().write("{\"message\":\"Logged out\"}");
                })
        );

        return http.build();
    }
    
}

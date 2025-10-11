package org.example.config;

import org.example.repository.UserRepository;
import org.example.security.BruteForceProtectionService;
import org.example.security.LockingAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthConfig {


    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[0]))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }



    // Expose AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public BruteForceProtectionService bruteForceProtectionService() {
        return new BruteForceProtectionService();
    }

    @Bean
    public LockingAuthenticationProvider authenticationProvider(
            UserDetailsService uds,
            PasswordEncoder encoder,
            BruteForceProtectionService bruteForce
    ) {
        return new LockingAuthenticationProvider(uds, encoder, bruteForce);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {   // ⬅️ moved here
        return new BCryptPasswordEncoder();
    }
}

package org.example.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LockingAuthenticationProvider extends DaoAuthenticationProvider {
    private final BruteForceProtectionService bruteForce;

    public LockingAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            BruteForceProtectionService bruteForce
    ){
        super(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
        this.bruteForce = bruteForce;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = (authentication.getPrincipal() == null)
                ? null
                : authentication.getName();

        //Pre-check
        if (bruteForce.isBlocked(username)){
            long sec = bruteForce.secondsUntilUnlock(username);
            throw new LockedException("User locked due to failed logins. Try again in ~" + sec + "s");
        }

        try {
            Authentication result = super.authenticate(authentication);
            //Success
            bruteForce.registerLoginSuccess(username);
            return result;
        }catch (BadCredentialsException exception){
            //Record failure
            bruteForce.registerLoginFailure(username);
            throw exception;
        }catch (RuntimeException exception){
            bruteForce.registerLoginFailure(username);
            throw exception;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

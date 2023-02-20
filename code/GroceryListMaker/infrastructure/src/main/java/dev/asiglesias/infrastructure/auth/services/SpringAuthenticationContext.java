package dev.asiglesias.infrastructure.auth.services;

import dev.asiglesias.application.auth.services.AuthenticationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SpringAuthenticationContext implements AuthenticationContext {
    @Override
    public String getUsername() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

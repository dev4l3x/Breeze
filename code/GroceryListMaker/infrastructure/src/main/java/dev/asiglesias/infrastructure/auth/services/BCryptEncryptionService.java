package dev.asiglesias.infrastructure.auth.services;

import dev.asiglesias.application.auth.services.EncryptionService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("bcrypt")
@Primary
public class BCryptEncryptionService implements EncryptionService {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptEncryptionService() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encrypt(String text) {
        return passwordEncoder.encode(text);
    }
}

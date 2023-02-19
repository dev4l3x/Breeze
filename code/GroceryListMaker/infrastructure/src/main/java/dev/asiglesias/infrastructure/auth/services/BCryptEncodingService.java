package dev.asiglesias.infrastructure.auth.services;

import dev.asiglesias.application.auth.services.EncodingService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("bcrypt")
@Primary
public class BCryptEncodingService implements EncodingService {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptEncodingService() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(String text) {
        return passwordEncoder.encode(text);
    }

    @Override
    public boolean matches(String text, String encodedText) {
        return passwordEncoder.matches(text, encodedText);
    }


}

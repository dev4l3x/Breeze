package dev.asiglesias.infrastructure.auth.services;

import dev.asiglesias.application.auth.services.EncryptionService;
import org.springframework.stereotype.Service;

@Service("plain")
public class PlainTextEncryptionService implements EncryptionService {
    @Override
    public String encrypt(String text) {
        return text;
    }
}

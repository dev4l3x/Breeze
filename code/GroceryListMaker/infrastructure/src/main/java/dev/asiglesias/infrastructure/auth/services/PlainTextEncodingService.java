package dev.asiglesias.infrastructure.auth.services;

import dev.asiglesias.application.auth.services.EncodingService;
import org.springframework.stereotype.Service;

@Service("plain")
public class PlainTextEncodingService implements EncodingService {
    @Override
    public String encode(String text) {
        return text;
    }

    @Override
    public boolean matches(String text, String encodedText) {
        return text.equals(encodedText);
    }
}

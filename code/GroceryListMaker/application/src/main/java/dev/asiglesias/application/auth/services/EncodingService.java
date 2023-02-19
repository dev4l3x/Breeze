package dev.asiglesias.application.auth.services;

public interface EncodingService {

    String encode(String text);

    boolean matches(String text, String encodedText);

}

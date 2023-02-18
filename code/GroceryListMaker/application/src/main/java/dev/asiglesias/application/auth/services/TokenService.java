package dev.asiglesias.application.auth.services;

public interface TokenService {

    String createTokenForUsername(String user);

    String getUser(String token);

    boolean isValid(String token);
}

package dev.asiglesias.application.auth.services;

public interface EncryptionService {

    String encrypt(String textToEncrypt);

    String decrypt(String textToDecrypt);

}

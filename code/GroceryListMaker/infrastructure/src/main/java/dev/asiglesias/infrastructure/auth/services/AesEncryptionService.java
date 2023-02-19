package dev.asiglesias.infrastructure.auth.services;

import dev.asiglesias.application.auth.services.EncryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AesEncryptionService implements EncryptionService {

    public static final String ENCRYPTION_ALGORITHM = "AES/ECB/PKCS5Padding";
    private final SecretKeySpec secretKey;

    public AesEncryptionService(@Value("${app.security.encryption.aes.secret}") String secret) throws Exception {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");

    }

    @Override
    public String encrypt(String textToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new RuntimeException("Error while encrypting", exception);
        }
    }

    @Override
    public String decrypt(String textToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(textToDecrypt)));
        } catch (Exception exception) {
            throw new RuntimeException("Error while decrypting", exception);
        }
    }
}

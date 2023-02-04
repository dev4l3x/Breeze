package dev.asiglesias.infrastructure.service.auth;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class TokenService {

    public static final String HMAC_SHA_256 = "HmacSHA256";

    @Value("${app.security.jwt.secret}")
    private String secret;

    public String createTokenForUser(String user) {
        Key key = new SecretKeySpec(secret.getBytes(), 0, secret.length(), HMAC_SHA_256);
        return Jwts.builder()
                .setSubject(user)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 1000000000000L))
                .signWith(key)
                .compact();
    }

    public String getUser(String token) {
        Key key = new SecretKeySpec(secret.getBytes(), 0, secret.length(), HMAC_SHA_256);
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
        Jws<Claims> claims = parser.parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    public boolean isValid(String token) {
        Key key = new SecretKeySpec(secret.getBytes(), 0, secret.length(), HMAC_SHA_256);
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        try {
           parser.parseClaimsJws(token);
        } catch (ExpiredJwtException expiredJwtException) {
           log.debug("Token expired");
           return false;
        }

        return parser.isSigned(token) ;
    }

}

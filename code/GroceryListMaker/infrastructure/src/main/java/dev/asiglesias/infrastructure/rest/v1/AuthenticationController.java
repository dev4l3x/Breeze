package dev.asiglesias.infrastructure.rest.v1;

import dev.asiglesias.infrastructure.repositories.jpa.UserRepository;
import dev.asiglesias.infrastructure.rest.v1.dto.LoginResponseDto;
import dev.asiglesias.infrastructure.rest.v1.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsManager userDetailsManager;

    @PostMapping("signin")
    public ResponseEntity<LoginResponseDto> login() {
        String secret = "testtesttestetseasdjfl;kasjdf;lasjdf;lajsdf;lja;lkjfelkjwer";
        Key key = new SecretKeySpec(secret.getBytes(), 0, secret.length(), "HmacSHA256");

        String jwt = Jwts.builder()
                .setSubject(SecurityContextHolder.getContext().getAuthentication().getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 10000))
                .signWith(key)
                .compact();

        return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.builder().access_token(jwt).build());
    }

    @PostMapping("signup")
    public ResponseEntity<Void> register(@RequestBody UserDTO userToCreate) {

        boolean existsUser = userDetailsManager.userExists(userToCreate.username());

        if (existsUser) {
            throw new AuthenticationServiceException("Username already exists");
        }

        UserDetails user = User.builder()
                .username(userToCreate.username())
                .password(passwordEncoder.encode(userToCreate.password()))
                .roles("USER")
                .build();
        userDetailsManager.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("test")
    public ResponseEntity<Void> testAuthentication() {
        return ResponseEntity.ok().build();
    }

}

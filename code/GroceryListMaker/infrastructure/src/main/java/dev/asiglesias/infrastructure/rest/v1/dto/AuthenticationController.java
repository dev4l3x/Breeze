package dev.asiglesias.infrastructure.rest.v1.dto;

import dev.asiglesias.infrastructure.repositories.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JdbcUserDetailsManager userDetailsManager;

    @PostMapping("login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> register(@RequestBody UserDTO userToCreate) {
        UserDetails user = User.builder()
                .username(userToCreate.username())
                .password(passwordEncoder.encode(userToCreate.password()))
                .roles("USER")
                .build();
        userDetailsManager.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

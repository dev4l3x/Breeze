package dev.asiglesias.infrastructure.rest.v1.dto;

import dev.asiglesias.infrastructure.repositories.jpa.UserRepository;
import dev.asiglesias.infrastructure.repositories.jpa.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;

    @PostMapping("login")
    public ResponseEntity<Void> login() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("register")
    public ResponseEntity<Void> register() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

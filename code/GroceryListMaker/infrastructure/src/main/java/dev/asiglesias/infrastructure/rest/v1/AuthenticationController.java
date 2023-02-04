package dev.asiglesias.infrastructure.rest.v1;

import dev.asiglesias.infrastructure.repositories.jpa.UserRepository;
import dev.asiglesias.infrastructure.repositories.jpa.entities.User;
import dev.asiglesias.infrastructure.rest.v1.dto.LoginResponseDto;
import dev.asiglesias.infrastructure.rest.v1.dto.UserDTO;
import dev.asiglesias.infrastructure.service.auth.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsManager userDetailsManager;

    private final TokenService tokenService;

    @PostMapping("signin")
    public ResponseEntity<LoginResponseDto> login() {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String jwt = tokenService.createTokenForUser(user.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.builder().access_token(jwt).build());
    }

    @PostMapping("signup")
    public ResponseEntity<Void> register(@RequestBody UserDTO userToCreate) {

        boolean existsUser = userDetailsManager.userExists(userToCreate.username());

        if (existsUser) {
            throw new AuthenticationServiceException("Username already exists");
        }

        UserDetails user = org.springframework.security.core.userdetails.User.builder()
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

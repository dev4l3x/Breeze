package dev.asiglesias.infrastructure.auth.controllers;

import dev.asiglesias.application.auth.CreateUserUseCase;
import dev.asiglesias.application.auth.CreateUserUseCaseImpl;
import dev.asiglesias.application.auth.SignInUserUseCase;
import dev.asiglesias.application.auth.SignInUserUseCaseImpl;
import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.infrastructure.auth.repositories.usecases.UserRepositoryImpl;
import dev.asiglesias.infrastructure.auth.services.JwtTokenService;
import dev.asiglesias.infrastructure.rest.v1.dto.LoginResponseDto;
import dev.asiglesias.infrastructure.rest.v1.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {


    private final CreateUserUseCase createUserUseCase;

    private final SignInUserUseCase signInUserUseCase;

    public AuthenticationController(UserRepositoryImpl userRepository, JwtTokenService tokenService, EncryptionService encryptionService) {
        createUserUseCase = new CreateUserUseCaseImpl(userRepository, encryptionService);
        signInUserUseCase = new SignInUserUseCaseImpl(encryptionService, userRepository, tokenService);
    }

    @PostMapping("signin")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDTO userDTO) {

        UserDataContainer userDataContainer = new UserDataContainer(userDTO.username(), userDTO.password());

        String jwt = signInUserUseCase.signIn(userDataContainer);

        return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.builder().access_token(jwt).build());
    }

    @PostMapping("signup")
    public ResponseEntity<Void> register(@RequestBody UserDTO userToCreate) {

        UserDataContainer userDataContainer = new UserDataContainer(userToCreate.username(), userToCreate.password());

        createUserUseCase.createUser(userDataContainer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("test")
    public ResponseEntity<Void> testAuthentication() {
        return ResponseEntity.ok().build();
    }

}

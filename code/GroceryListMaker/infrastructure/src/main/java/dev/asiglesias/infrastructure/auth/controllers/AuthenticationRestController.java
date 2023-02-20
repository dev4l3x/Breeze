package dev.asiglesias.infrastructure.auth.controllers;

import dev.asiglesias.application.auth.CreateUserUseCase;
import dev.asiglesias.application.auth.CreateUserUseCaseImpl;
import dev.asiglesias.application.auth.SignInUserUseCase;
import dev.asiglesias.application.auth.SignInUserUseCaseImpl;
import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.services.EncodingService;
import dev.asiglesias.infrastructure.auth.repositories.usecases.UserRepositoryImpl;
import dev.asiglesias.infrastructure.auth.services.JwtTokenService;
import dev.asiglesias.infrastructure.grocerylist.controllers.dto.LoginResponseDto;
import dev.asiglesias.infrastructure.auth.controllers.dto.rest.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
public class AuthenticationRestController {


    private final CreateUserUseCase createUserUseCase;

    private final SignInUserUseCase signInUserUseCase;

    public AuthenticationRestController(UserRepositoryImpl userRepository, JwtTokenService tokenService, EncodingService encodingService) {
        createUserUseCase = new CreateUserUseCaseImpl(userRepository, encodingService);
        signInUserUseCase = new SignInUserUseCaseImpl(encodingService, userRepository, tokenService);
    }

    @PostMapping("signin")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserDTO userDTO) {

        UserDataContainer userDataContainer = UserDataContainer.builder()
                .username(userDTO.username())
                .password(userDTO.password())
                .build();

        String jwt = signInUserUseCase.signIn(userDataContainer);

        return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.builder().access_token(jwt).build());
    }

    @PostMapping("signup")
    public ResponseEntity<Void> register(@RequestBody UserDTO userToCreate) {

        UserDataContainer userDataContainer = UserDataContainer.builder()
                .username(userToCreate.username())
                .password(userToCreate.password())
                .build();

        createUserUseCase.createUser(userDataContainer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("test")
    public ResponseEntity<Void> testAuthentication() {
        return ResponseEntity.ok().build();
    }

}

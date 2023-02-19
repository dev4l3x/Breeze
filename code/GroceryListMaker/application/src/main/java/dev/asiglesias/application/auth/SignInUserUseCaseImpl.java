package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncodingService;
import dev.asiglesias.application.auth.services.TokenService;
import dev.asiglesias.application.exceptions.InvalidParameterException;
import dev.asiglesias.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class SignInUserUseCaseImpl implements SignInUserUseCase {

    private final EncodingService encodingService;

    private final UserRepository userRepository;

    private final TokenService tokenService;

    @Override
    public String signIn(UserDataContainer userDataContainer) {

        if (userDataContainer == null
                ||userDataContainer.getUsername() == null
                || userDataContainer.getUsername().isBlank()
                || userDataContainer.getPassword() == null
                || userDataContainer.getPassword().isBlank()) {
            throw new InvalidParameterException("User must have username and password");
        }

        Optional<User> user = userRepository.findByUsername(userDataContainer.getUsername());

        if (user.isEmpty()) {
            throw new InvalidParameterException("Username not found");
        }

        if (!encodingService.matches(userDataContainer.getPassword(), user.get().getPassword())) {
            throw new InvalidParameterException("Invalid password");
        }

        return tokenService.createTokenForUsername(user.get().getUsername());

    }
}

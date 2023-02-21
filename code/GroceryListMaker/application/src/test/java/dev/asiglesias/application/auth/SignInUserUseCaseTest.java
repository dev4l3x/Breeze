package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncodingService;
import dev.asiglesias.application.auth.services.TokenService;
import dev.asiglesias.application.exceptions.InvalidParameterException;
import dev.asiglesias.domain.User;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignInUserUseCaseTest {

    public static final String TEST_USERNAME = "username";
    public static final String TEST_PASSWORD = "password";
    public static final String ENCRYPTED_PASSWORD = "encrypted_password";
    public static final String TEST_TOKEN = "test-token";
    @Mock
    private EncodingService encodingService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SignInUserUseCaseImpl signInUserUseCase;

    @Test
    void givenUsernameDoesNotExists_whenSignIn_thenThrowInvalidParameter() {
        //Arrange
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        //Act && Assert
        assertThrows(InvalidParameterException.class, () -> signInUserUseCase.signIn(userDataContainer));
    }

    @NullSource
    @MethodSource
    @ParameterizedTest
    void givenInvalidInput_whenSignIn_thenThrowInvalidParameter(UserDataContainer userDataContainer) {
        //Act && Assert
        assertThrows(InvalidParameterException.class, () -> signInUserUseCase.signIn(userDataContainer));
    }

    public static Stream<Arguments> givenInvalidInput_whenSignIn_thenThrowInvalidParameter() {
        return Stream.of(
                Arguments.of(Named.of("Without username and password", UserDataContainer.builder().build())),
                Arguments.of(Named.of("Without username", UserDataContainer.builder().password(TEST_PASSWORD).build())),
                Arguments.of(Named.of("Without password", UserDataContainer.builder().username(TEST_USERNAME).build()))
        );
    }

    @Test
    void givenUserExistsButInvalidPassword_whenSignIn_thenThrowInvalidParameter() {
        //Arrange
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .build();
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(ENCRYPTED_PASSWORD)
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(encodingService.matches(TEST_PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(false);

        //Act && Assert
        assertThrows(InvalidParameterException.class, () -> signInUserUseCase.signIn(userDataContainer));
    }

    @Test
    void givenUserExistsAndPasswordMatches_whenSignIn_thenReturnToken() {
        //Arrange
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .password(TEST_PASSWORD)
                .username(TEST_USERNAME)
                .build();
        User user = User.builder()
                .username(TEST_USERNAME)
                .password(ENCRYPTED_PASSWORD)
                .build();

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(user));
        when(encodingService.matches(TEST_PASSWORD, ENCRYPTED_PASSWORD)).thenReturn(true);
        when(tokenService.createTokenForUsername(user.getUsername())).thenReturn(TEST_TOKEN);

        //Act
        String token = signInUserUseCase.signIn(userDataContainer);

        //Assert
        assertEquals(TEST_TOKEN, token);
    }

}
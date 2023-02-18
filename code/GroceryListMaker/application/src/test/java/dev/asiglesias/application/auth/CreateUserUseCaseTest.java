package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;
import dev.asiglesias.application.auth.repositories.UserRepository;
import dev.asiglesias.application.auth.services.EncryptionService;
import dev.asiglesias.application.exceptions.InvalidParameterException;
import dev.asiglesias.domain.User;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    private static final String TEST_PASSWORD = "password";
    private static final String TEST_USERNAME = "username";
    public static final String ENCRYPTED_TEST_PASSWORD = "ENCRYPTED_TEST_PASSWORD";

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    CreateUserUseCaseImpl createUserUseCase;

    @NullSource
    @MethodSource
    @ParameterizedTest
    void givenUserWithInvalidData_whenCreateUser_thenThrowException(UserDataContainer userDataContainer) {
        //Act && Assert
        assertThrows(InvalidParameterException.class, () -> createUserUseCase.createUser(userDataContainer));
    }

    public static Stream<Arguments> givenUserWithInvalidData_whenCreateUser_thenThrowException() {
        return Stream.of(
                Arguments.of(Named.of("Without username and password", UserDataContainer.builder().build())),
                Arguments.of(Named.of("Without username", UserDataContainer.builder().password(TEST_PASSWORD).build())),
                Arguments.of(Named.of("Without password", UserDataContainer.builder().username(TEST_USERNAME).build()))
        );
    }

    @Test
    void givenUserWithoutUsername_whenCreateUser_thenThrowException() {
        //Arrange
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .username(TEST_USERNAME)
                .password(null)
                .build();

        //Act && Assert
        assertThrows(InvalidParameterException.class, () -> createUserUseCase.createUser(userDataContainer));
    }

    @Test
    void givenUserAlreadyExists_whenCreateUser_thenThrowException() {
        //Arrange
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.existsUsername(TEST_USERNAME)).thenReturn(true);

        //Act && Assert
        assertThrows(InvalidParameterException.class, () -> createUserUseCase.createUser(userDataContainer));
    }

    @Test
    void givenValidUser_whenCreateUser_thenEncryptPasswordBeforeSave() {
        //Arrange
        UserDataContainer userDataContainer = UserDataContainer.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.existsUsername(TEST_USERNAME)).thenReturn(false);
        when(encryptionService.encrypt(TEST_PASSWORD)).thenReturn(ENCRYPTED_TEST_PASSWORD);

        //Act
        createUserUseCase.createUser(userDataContainer);

        //Assert
        verify(encryptionService).encrypt(TEST_PASSWORD);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).createUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(ENCRYPTED_TEST_PASSWORD);
    }

}
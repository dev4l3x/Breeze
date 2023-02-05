package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;

public interface SignInUserUseCase {
    String signIn(UserDataContainer userDataContainer);
}

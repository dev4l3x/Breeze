package dev.asiglesias.application.auth;

import dev.asiglesias.application.auth.models.UserDataContainer;

public interface CreateUserUseCase {
    void createUser(UserDataContainer userDataContainer);
}

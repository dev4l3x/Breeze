package dev.asiglesias.application;

import dev.asiglesias.domain.User;

public interface GenerateGroceryListUseCase {
    void generateForUser(User user);
}

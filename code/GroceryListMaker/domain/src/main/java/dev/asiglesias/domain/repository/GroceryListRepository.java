package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.GroceryList;
import dev.asiglesias.domain.User;

public interface GroceryListRepository {
    void createForUser(GroceryList groceryList, User user);
}

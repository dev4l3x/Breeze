package dev.asiglesias.domain.repository;

import dev.asiglesias.domain.GroceryList;

public interface GroceryListRepository {
    void createForUser(GroceryList groceryList);
}

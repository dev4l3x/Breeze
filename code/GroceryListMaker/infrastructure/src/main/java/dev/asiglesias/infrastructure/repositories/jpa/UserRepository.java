package dev.asiglesias.infrastructure.repositories.jpa;

import dev.asiglesias.infrastructure.repositories.jpa.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}

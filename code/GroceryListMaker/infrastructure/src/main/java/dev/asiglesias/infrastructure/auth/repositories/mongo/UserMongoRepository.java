package dev.asiglesias.infrastructure.auth.repositories.mongo;

import dev.asiglesias.infrastructure.auth.entitites.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserMongoRepository extends CrudRepository<User, String> {
}

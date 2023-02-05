package dev.asiglesias.infrastructure.auth.repositories.mongo;

import dev.asiglesias.infrastructure.auth.entitites.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserMongoRepository extends MongoRepository<User, String> {
}

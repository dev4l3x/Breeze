package dev.asiglesias.infrastructure.repositories.jpa;

import dev.asiglesias.infrastructure.repositories.jpa.entities.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Optional;

@Slf4j
public class InMemoryUserRepository implements UserRepository {

    private final User user;

    public InMemoryUserRepository() {
        log.debug("Created InMemoryUserRepository");
        User u = new User();
        u.setUsername("test");
        u.setPassword("test");
        user = u;
    }

    @Override
    public <S extends User> S save(S entity) {
        return entity;
    }

    @Override
    public <S extends User> Iterable<S> saveAll(Iterable<S> entities) {
        return entities;
    }

    @Override
    public Optional<User> findById(String s) {
        return Optional.of(user);
    }

    @Override
    public boolean existsById(String s) {
        return user.getUsername().equals(s);
    }

    @Override
    public Iterable<User> findAll() {
        return Collections.singleton(user);
    }

    @Override
    public Iterable<User> findAllById(Iterable<String> strings) {
        return Collections.singleton(user);
    }

    @Override
    public long count() {
        return 1;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(User entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {

    }

    @Override
    public void deleteAll() {

    }
}

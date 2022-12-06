package dev.asiglesias.infrastructure.service.auth;

import dev.asiglesias.infrastructure.repositories.jpa.UserRepository;
import dev.asiglesias.infrastructure.repositories.jpa.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class PostgresUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("Username {} not found", username)));
    }

    @Override
    public void createUser(UserDetails user) {
        User userToCreate = new User();
        userToCreate.setPassword(user.getPassword());
        userToCreate.setUsername(user.getUsername());
        userToCreate.setEnabled(true);
        userRepository.save(userToCreate);
    }

    @Override
    public void updateUser(UserDetails user) {
    }

    @Override
    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsById(username);
    }
}

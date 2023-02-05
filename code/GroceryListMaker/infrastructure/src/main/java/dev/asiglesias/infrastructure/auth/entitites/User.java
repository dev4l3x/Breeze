package dev.asiglesias.infrastructure.auth.entitites;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class User {

    @Id
    private String username;

    private String password;

    private boolean enabled;

}

package dev.asiglesias.infrastructure.repositories.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "users")
@Getter
@Setter
public class User {

    @Id
    private String username;

    private String password;

}

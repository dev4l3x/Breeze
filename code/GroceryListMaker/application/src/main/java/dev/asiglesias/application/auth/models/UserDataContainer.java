package dev.asiglesias.application.auth.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserDataContainer {
    String username;

    String password;
}

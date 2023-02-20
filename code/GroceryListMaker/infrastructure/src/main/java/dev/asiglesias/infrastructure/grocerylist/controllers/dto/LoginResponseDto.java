package dev.asiglesias.infrastructure.grocerylist.controllers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String access_token;
}

package dev.asiglesias.infrastructure.rest.v1.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String access_token;
}

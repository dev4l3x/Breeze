package dev.asiglesias.infrastructure.spring;

import lombok.Value;

@Value
public class ErrorDTO {
    String code;
    String message;
}

package dev.asiglesias.application.exceptions;

import dev.asiglesias.domain.exception.DomainException;

public class InvalidParameterException extends DomainException {
    public InvalidParameterException(String message) {
        super("INVALID_PARAMETER", message);
    }
}

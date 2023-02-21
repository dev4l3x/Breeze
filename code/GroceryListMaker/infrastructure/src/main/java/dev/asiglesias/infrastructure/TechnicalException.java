package dev.asiglesias.infrastructure;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {

    private String code;

    public TechnicalException(String code, String message) {
        super(message);
        this.code = code;
    }

}

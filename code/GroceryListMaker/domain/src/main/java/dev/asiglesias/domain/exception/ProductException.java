package dev.asiglesias.domain.exception;

import dev.asiglesias.domain.exception.code.ProductExceptionCode;

public class ProductException extends DomainException {

    public ProductException(ProductExceptionCode code, String message) {
        super(code.name(), message);
    }
}

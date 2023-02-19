package dev.asiglesias.infrastructure.spring;

import dev.asiglesias.domain.exception.DomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = {DomainException.class})
    public ResponseEntity<ErrorDTO> handleFunctionalException(DomainException ex) {
        ErrorDTO error = new ErrorDTO(ex.getCode(), ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

}

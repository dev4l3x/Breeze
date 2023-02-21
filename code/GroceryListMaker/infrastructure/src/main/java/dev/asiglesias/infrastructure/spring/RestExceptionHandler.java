package dev.asiglesias.infrastructure.spring;

import dev.asiglesias.domain.exception.DomainException;
import dev.asiglesias.infrastructure.TechnicalException;
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

    @ExceptionHandler(value = {TechnicalException.class})
    public ResponseEntity<ErrorDTO> handleTechnicalException(TechnicalException ex) {
        ErrorDTO error = new ErrorDTO(ex.getCode(), ex.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        ErrorDTO error = new ErrorDTO("INTERNAL", "An error has occurred");
        return ResponseEntity.internalServerError().body(error);
    }

}

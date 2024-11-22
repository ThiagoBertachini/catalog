package com.tbemerencio.catalog.controllers.exceptions;

import com.tbemerencio.catalog.services.exceptions.DataBaseIntegrityException;
import com.tbemerencio.catalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
    private final int UNPROCESSABLE_ENTITY = HttpStatus.UNPROCESSABLE_ENTITY.value();

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DefaultException> entityNotFound(ResourceNotFoundException error,
                                                           HttpServletRequest request){
        DefaultException exceptionResponse = new DefaultException();
        exceptionResponse.setTimestamp(Instant.now());
        exceptionResponse.setMessage(error.getMessage());
        exceptionResponse.setError("Resource not found");
        exceptionResponse.setPath(request.getRequestURI());
        exceptionResponse.setStatus(NOT_FOUND);
        return ResponseEntity.status(NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(DataBaseIntegrityException.class)
    public ResponseEntity<DefaultException> deletionException(DataBaseIntegrityException error,
                                                           HttpServletRequest request){
        DefaultException exceptionResponse = new DefaultException();
        exceptionResponse.setTimestamp(Instant.now());
        exceptionResponse.setMessage(error.getMessage());
        exceptionResponse.setError("Deletion not allowed");
        exceptionResponse.setPath(request.getRequestURI());
        exceptionResponse.setStatus(UNPROCESSABLE_ENTITY);
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(exceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultException> validationException(MethodArgumentNotValidException error,
                                                           HttpServletRequest request){
        ValidationException exceptionResponse = new ValidationException();
        error.getBindingResult().getFieldErrors().forEach(
                err -> exceptionResponse.addError(err.getField(), err.getDefaultMessage()));
        exceptionResponse.setTimestamp(Instant.now());
        exceptionResponse.setMessage(error.getMessage());
        exceptionResponse.setError("Validation error");
        exceptionResponse.setPath(request.getRequestURI());
        exceptionResponse.setStatus(UNPROCESSABLE_ENTITY);
        return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(exceptionResponse);
    }
}

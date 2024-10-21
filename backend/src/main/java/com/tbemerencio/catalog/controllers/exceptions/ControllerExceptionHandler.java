package com.tbemerencio.catalog.controllers.exceptions;

import com.tbemerencio.catalog.services.exceptions.EntityNotFoundException;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final int NOT_FOUND = HttpStatus.NOT_FOUND.value();

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DefaultException> entityNotFound(EntityNotFoundException error,
                                                           HttpServletRequest request){
        DefaultException exceptionResponse = new DefaultException();
        exceptionResponse.setTimestamp(Instant.now());
        exceptionResponse.setMessage(error.getMessage());
        exceptionResponse.setError("Resource not found");
        exceptionResponse.setPath(request.getRequestURI());
        exceptionResponse.setStatus(NOT_FOUND);
        return ResponseEntity.status(NOT_FOUND).body(exceptionResponse);
    }
}

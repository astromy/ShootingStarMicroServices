package com.astromyllc.shootingstar.hr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Deliberately limited to the exception types that carry a message meant for
// the user - everything else keeps Spring's default behaviour.
@RestControllerAdvice
public class HrExceptionHandler {

    @ExceptionHandler(KeycloakAccountConflictException.class)
    public ResponseEntity<Map<String, String>> handleKeycloakConflict(KeycloakAccountConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(HrValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidation(HrValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
}

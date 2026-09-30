package com.astromyllc.shootingstar.accommodation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Without this, every IllegalArgumentException/IllegalStateException thrown
// from the service layer (bad input, room at capacity, block not found,
// etc.) surfaced as a generic 500 with a raw stack trace - correct
// information, but not something a caller (ORB's proxy, or a frontend)
// could reasonably show to a user. This maps them to a clean 400 with the
// actual message instead.
@RestControllerAdvice
@Slf4j
public class AccommodationExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Accommodation request rejected: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        log.warn("Accommodation request rejected: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }
}

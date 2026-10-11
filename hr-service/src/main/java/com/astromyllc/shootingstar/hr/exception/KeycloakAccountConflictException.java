package com.astromyllc.shootingstar.hr.exception;

// Thrown when Keycloak refuses to create a staff member's login because the
// username or email is already taken. It carries a message an administrator
// can act on, which HrExceptionHandler returns to the Permissions screen
// instead of a bare 500.
public class KeycloakAccountConflictException extends RuntimeException {
    public KeycloakAccountConflictException(String message) {
        super(message);
    }
}

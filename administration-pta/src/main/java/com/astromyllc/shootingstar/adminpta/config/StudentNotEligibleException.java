package com.astromyllc.shootingstar.adminpta.config;

// Thrown by GateEventService when a gate event can't be recorded because
// the student doesn't exist or doesn't belong to the claimed institution.
// Message is safe to surface to the mobile client as-is.
public class StudentNotEligibleException extends RuntimeException {
    public StudentNotEligibleException(String message) {
        super(message);
    }
}

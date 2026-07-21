package com.astromyllc.shootingstar.hr.config;

// Thrown by ClockEventService when a clock-in/out can't be recorded because
// the staff code doesn't exist or doesn't belong to the claimed institution.
// Message is safe to surface to the mobile client as-is.
public class StaffNotEligibleException extends RuntimeException {
    public StaffNotEligibleException(String message) {
        super(message);
    }
}
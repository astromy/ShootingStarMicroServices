package com.astromyllc.shootingstar.hr.exception;

// A request the user can fix (bad dates, no balance left, ...). Its message is
// shown to them as-is, so write it for a person, not a log.
public class HrValidationException extends RuntimeException {
    public HrValidationException(String message) {
        super(message);
    }
}

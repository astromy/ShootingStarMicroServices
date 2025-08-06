package com.astromyllc.shootingstar.adminpta.config;

public class EmailProcessingException extends RuntimeException {
    public EmailProcessingException(String message) {
        super(message);
    }

    public EmailProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

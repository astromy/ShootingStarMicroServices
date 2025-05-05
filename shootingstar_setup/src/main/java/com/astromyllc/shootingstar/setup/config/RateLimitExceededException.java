package com.astromyllc.shootingstar.setup.config;

public class RateLimitExceededException  extends RuntimeException {
    public RateLimitExceededException() {
        super("Email rate limit exceeded");
    }

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}


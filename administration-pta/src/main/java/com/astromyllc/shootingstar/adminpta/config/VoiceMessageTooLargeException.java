package com.astromyllc.shootingstar.adminpta.config;

// Thrown by VoiceMessageService when a recording exceeds the enforced
// duration/size ceiling. Message is safe to surface to the user as-is.
public class VoiceMessageTooLargeException extends RuntimeException {
    public VoiceMessageTooLargeException(String message) {
        super(message);
    }
}
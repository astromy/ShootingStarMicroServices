package com.astromyllc.astroorb.utils;

import org.springframework.stereotype.Component;

@Component
public class TokenHolder {
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();

    public static String getToken() {
        return currentToken.get();
    }

    public static void setToken(String token) {
        currentToken.set(token);
    }

    public static void clear() {
        currentToken.remove();
    }

    public static boolean hasToken() {
        return currentToken.get() != null && !currentToken.get().isEmpty();
    }
}

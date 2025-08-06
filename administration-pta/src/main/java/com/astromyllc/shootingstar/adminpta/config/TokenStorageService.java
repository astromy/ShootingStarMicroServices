package com.astromyllc.shootingstar.adminpta.config;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenStorageService {

    private final Map<String, TokenData> tokenStore = new HashMap<>();

    public String storeToken(ActivationData data, Duration expiration) {
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, new TokenData(data, Instant.now().plus(expiration)));
        return token;
    }

    public ActivationData validateToken(String token) {
        TokenData tokenData = tokenStore.get(token);
        if (tokenData == null || Instant.now().isAfter(tokenData.getExpiry())) {
            tokenStore.remove(token);
            return null;
        }
        tokenStore.remove(token);
        return tokenData.getData();
    }

    // Inner classes with proper getters
    public static class TokenData {
        private final ActivationData data;
        private final Instant expiry;

        public TokenData(ActivationData data, Instant expiry) {
            this.data = data;
            this.expiry = expiry;
        }

        // Proper getter methods
        public ActivationData getData() {
            return data;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }

    public static class ActivationData {
        private final String studentId;
        private final String email;
        private final String amount;

        public ActivationData(String studentId, String email, String amount) {
            this.studentId = studentId;
            this.email = email;
            this.amount = amount;
        }

        // Proper getter methods
        public String getStudentId() {
            return studentId;
        }

        public String getEmail() {
            return email;
        }

        public String getAmount() {
            return amount;
        }
    }
}

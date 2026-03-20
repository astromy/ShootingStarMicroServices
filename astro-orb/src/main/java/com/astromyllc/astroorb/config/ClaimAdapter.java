package com.astromyllc.astroorb.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {
    @Override
    public Map<String, Object> convert(Map<String, Object> claims) {
        Map<String, Object> convertedClaims = new HashMap<>(claims);

        // Handle different claim names between web and mobile tokens
        if (claims.containsKey("allowed-origins")) {
            convertedClaims.put("allowedOrigins", claims.get("allowed-origins"));
        }

        // Ensure authorities claim exists
        if (!convertedClaims.containsKey("roles") && claims.containsKey("realm_access")) {
            Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                convertedClaims.put("roles", realmAccess.get("roles"));
            }
        }

        return convertedClaims;
    }
}
package com.astromyllc.astroorb.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class TokenDebugController {

    @GetMapping("/token-info")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> getTokenInfo(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> info = new HashMap<>();
        info.put("subject", jwt.getSubject());
        info.put("audience", jwt.getAudience());
        info.put("issuer", jwt.getIssuer());
        info.put("expiresAt", jwt.getExpiresAt());
        info.put("issuedAt", jwt.getIssuedAt());
        info.put("claims", jwt.getClaims().keySet());

        // Check which client issued the token
        info.put("clientId", jwt.getClaimAsString("azp"));
        info.put("authorizedParty", jwt.getClaimAsString("clientId"));

        return info;
    }
}
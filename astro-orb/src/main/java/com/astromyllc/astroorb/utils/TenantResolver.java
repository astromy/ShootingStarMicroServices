package com.astromyllc.astroorb.utils;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

/**
 * Resolves the tenant (institution code) and the acting user for the signed-in
 * session.
 *
 * The institution code always comes from the Keycloak "institution_group" claim
 * on the authenticated principal - never from a request body - so one school's
 * staff can't request another school's data by editing a request. Same parsing
 * as UserController#getIndex: "[/INST001]" or "[/INST001, /OTHER]" -> "INST001".
 */
public final class TenantResolver {

    private TenantResolver() {
    }

    public static Optional<String> institutionCode(OAuth2User principal) {
        if (principal == null) {
            return Optional.empty();
        }
        Object group = principal.getAttribute("institution_group");
        if (group == null) {
            return Optional.empty();
        }
        try {
            String code = group.toString().split(",")[0].split("/")[1].replace("]", "").trim();
            return code.isEmpty() ? Optional.empty() : Optional.of(code);
        } catch (ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * The signed-in user's name for audit fields (approved by, paid by, ...):
     * the full name if Keycloak has one, else the username.
     */
    public static Optional<String> userDisplayName(OAuth2User principal) {
        if (principal == null) {
            return Optional.empty();
        }
        for (String claim : new String[]{"name", "preferred_username"}) {
            Object value = principal.getAttribute(claim);
            if (value != null && !value.toString().isBlank()) {
                return Optional.of(value.toString().trim());
            }
        }
        return Optional.ofNullable(principal.getName()).filter(n -> !n.isBlank());
    }
}

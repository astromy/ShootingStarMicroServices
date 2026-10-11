package com.astromyllc.astroorb.utils;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Locale;

/**
 * Server-side check of the permissions the Permissions screen hands out (e.g. "Human_Resource leave_approvals").
 *
 * The browser hides menu items using the signed-in user's "groups" claim; this reads the SAME claim, so
 * whoever can see a menu item passes the check and whoever can't is refused even if they call the endpoint
 * directly. Comparison ignores case and extra spaces, like the menu does.
 */
public final class PermissionGuard {

    private PermissionGuard() {
    }

    public static boolean has(OAuth2User principal, String permission) {
        if (principal == null || permission == null) return false;
        Object groups = principal.getAttribute("groups");
        String wanted = normalise(permission);
        if (groups instanceof Collection<?> list) {
            for (Object g : list) {
                if (g != null && normalise(g.toString()).equals(wanted)) return true;
            }
            return false;
        }
        return groups != null && normalise(groups.toString()).equals(wanted);
    }

    private static String normalise(String s) {
        return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}

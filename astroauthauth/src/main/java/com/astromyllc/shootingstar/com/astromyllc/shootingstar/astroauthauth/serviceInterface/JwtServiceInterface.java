package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtServiceInterface {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}

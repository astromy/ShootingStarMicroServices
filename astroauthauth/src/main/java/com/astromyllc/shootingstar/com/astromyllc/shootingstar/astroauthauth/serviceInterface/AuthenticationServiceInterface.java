package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthenticationServiceInterface {
    UserDetailsService userDetailsService();
}

package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JwtAuthenticationResponse {
    private String token;
}

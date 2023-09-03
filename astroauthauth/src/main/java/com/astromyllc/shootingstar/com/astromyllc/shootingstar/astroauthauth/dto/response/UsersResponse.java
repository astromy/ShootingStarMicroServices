package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.response;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UsersResponse {
    private Long id;
    @Nonnull
    private String userName;
    @Nonnull
    private String email;
    @Nonnull
    private String userPassword;
    private String passwordHint;
    private LocalDateTime creationDate;
    private String userId;


}

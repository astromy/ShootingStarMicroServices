package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UsersRequest {
    private Long id;
    @Nonnull
    private String userName;
    @Nonnull
    private String email;
    @Nonnull
    private String userPassword;
    private String passwordHint;
    private String creationDate;
    private String userId;


}

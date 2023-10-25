package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.UserRole;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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
    private String password;
    private String passwordHint;
    private String creationDate;
    private String userId;
    private List<UserRole> role;

    private String firstName;
    private String lastName;
}

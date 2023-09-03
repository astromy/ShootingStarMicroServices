package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Nonnull
    @Column(unique = true)
    private String userName;
    @Nonnull
    @Column(unique = true)
    private String email;
    @Nonnull
    private String userPassword;
    private String passwordHint;
    private LocalDateTime creationDate;
    private String userId;


}

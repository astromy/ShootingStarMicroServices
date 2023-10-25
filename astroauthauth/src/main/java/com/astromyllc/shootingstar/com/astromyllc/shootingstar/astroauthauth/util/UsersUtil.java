package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.util;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.UserRole;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository.UsersRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersUtil {

    private final PasswordEncoder passwordEncoder;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public Users mapUserRequest_ToUsers(UsersRequest usersRequest) {
   return Users.builder()
           .creationDate(LocalDateTime.now())
           .email(usersRequest.getEmail())
           .passwordHint(usersRequest.getPasswordHint())
           .username(usersRequest.getUserName())
           .firstName(usersRequest.getFirstName())
           .lastName(usersRequest.getLastName())
           .password(passwordEncoder.encode(usersRequest.getPassword()))
           .role(UserRole.USER)
           .build();
    }
}

package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.controller;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.UsersServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UsersServiceInterface usersServiceInterface;
    @PostMapping("/api/astroauthauth/savenewuser")
    @ResponseStatus(HttpStatus.CREATED)
    public void checkCredentials(@RequestBody UsersRequest usersRequest) {
        usersServiceInterface.addUser(usersRequest );
    }
}

package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.response.JwtAuthenticationResponse;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;

import java.util.List;
import java.util.Optional;

public interface UsersServiceInterface {
    public List<Users> getAllUsers();
    public Users getUserById(int Userid);
    public Users addUser(Users User);
    public Optional<Users> getUserByName(String Username);
    public String getUserByNameAndPassword(String Username,String password);
    public void addUser(UsersRequest usersRequest);

    boolean isExist(Integer id, String name);

    public Boolean checkUser(UsersRequest usersRequest);

    JwtAuthenticationResponse signup(UsersRequest request);

    JwtAuthenticationResponse signin(UsersRequest request);
}

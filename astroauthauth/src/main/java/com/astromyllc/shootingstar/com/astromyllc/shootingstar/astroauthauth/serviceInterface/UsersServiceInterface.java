package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;

import java.util.List;

public interface UsersServiceInterface {
    public List<Users> getAllUsers();
    public Users getUserById(int Userid);
    public Users addUser(Users User);
    public Users getUserByName(String Username);
    public String getUserByNameAndPassword(String Username,String password);
    public boolean isExist(Integer id, String name);
}

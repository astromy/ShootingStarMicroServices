package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.response.JwtAuthenticationResponse;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository.UsersRepository;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.UsersServiceInterface;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.util.UsersUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class UsersService implements UsersServiceInterface {


    private final UsersRepository usersRepository;

    private final UsersUtil usersUtil;

    private final JwtService jwtService;

    //private AuthenticationManager authenticationManager;

    public static List<Users> usersGlobalList = new ArrayList<>();

    @Override
    public List<Users> getAllUsers() {
        return null;
    }

    @Override
    public Users getUserById(int Userid) {
        return null;
    }

    @Override
    public Users addUser(Users User) {
        return null;
    }

    public void addUser(UsersRequest usersRequest) {
        Optional<Users> users = usersRepository.findUserByUsername(usersRequest.getUserName());//.usersGlobalList.stream().filter(u -> u.getUsername().equals(usersRequest.getUserName())).findFirst();
        if (users.isEmpty()) {
            Users nUser = usersUtil.mapUserRequest_ToUsers(usersRequest);
            usersRepository.save(nUser);
            usersGlobalList.add(nUser);
        }
    }

    @Override
    public Optional<Users> getUserByName(String userName) {
        Optional<Users> user = usersRepository.findUserByUsername(userName);//usersUtil.usersGlobalList.stream().filter(u -> u.getUsername().equals(userName)).findFirst().get();// findUserByUsername(Username);
        if (user == null) {
            user = usersRepository.findUserByEmail(userName);
        }
        return user;
    }

    @Override
    public String getUserByNameAndPassword(String Username, String password) {
        return null;
    }

    @Override
    public boolean isExist(Integer id, String name) {
        return false;
    }

    @Override
    public Boolean checkUser(UsersRequest usersRequest) {
        return null;
    }

    @Override
    public JwtAuthenticationResponse signup(UsersRequest request) {
        Optional<Users> users = usersGlobalList.stream().filter(u -> u.getUsername().equals(request.getUserName())).findFirst(); //Optional.ofNullable(usersRepository.findUserByUsername(request.getUserName()));
        Users nUser = new Users();
        if (users.isEmpty()) {
            nUser = usersUtil.mapUserRequest_ToUsers(request);
            usersRepository.save(nUser);
            usersGlobalList.add(nUser);
        }

        var jwt = jwtService.generateToken(nUser);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(UsersRequest request) {
       /* authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));*/
        Users users1 = usersGlobalList.stream().filter(u -> u.getUsername().equals(request.getUserName())).findFirst().get();
        if (users1 == null) {
            users1 = usersGlobalList.stream().filter(u -> u.getEmail().equals(request.getEmail())).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        }
        var jwt = jwtService.generateToken(users1);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }


    //@Bean("4th")
    private void fetchAllUsers() {
        usersGlobalList = usersRepository.findAll();
        log.info("{} users RECORDS FETCHED", usersGlobalList.size());
    }
}

package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository.UsersRepository;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.UsersServiceInterface;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.util.UsersUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsersService implements UsersServiceInterface {


    private final UsersRepository usersRepository;
    private final UsersUtil usersUtil;

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

    @Override
    public Users getUserByName(String userName) {
       Optional<Users> user = usersUtil.usersGlobalList.stream().filter(u->u.getUserName().equals(userName)).findFirst();// findUserByUsername(Username);
       if(user.isEmpty()) return null;
       return user.get();
    }

    @Override
    public String getUserByNameAndPassword(String Username, String password) {
        return null;
    }

    @Override
    public boolean isExist(Integer id, String name) {
        return false;
    }

    /*
     {
        return (List<Users>) usersRepository.findAll();
    }
    {
        return usersRepository.findOne(Userid);
    }
    {
        return usersRepository.save(User);
    }
    {
        Users User = usersRepository.findUserByUsername(Username);
        if(User == null){
            return null;
        }
        return User;
    }

    {
        return UserRepository.findUserPassword(Username);
    }


	/*public List<Users> findAllEnabled(){
		return UserRepository.findAllEnabled();
	}

    {
        List<Users> UserUnique = usersRepository.findUserNamesUnique(id);
        List<String> names = new LinkedList<String>();

        for (Users u : (UserUnique)) {
            names.add(u.getUsername().toUpperCase());
        }
        return names.contains(name.toUpperCase());
    }
*/

}

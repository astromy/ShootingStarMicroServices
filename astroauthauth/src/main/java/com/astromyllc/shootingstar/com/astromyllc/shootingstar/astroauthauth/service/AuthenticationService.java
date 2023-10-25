package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository.UsersRepository;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.AuthenticationServiceInterface;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Service("authenticationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements AuthenticationServiceInterface {

    private final UsersRepository usersRepository;
    private final UsersService userAccountService;

    private final UsersService usersService;


/*
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userAccountService.getUserByName(username);
		if (user == null) {
			log.debug(username);
				throw new UsernameNotFoundException("Username or password not found");
		}

		log.debug("Load UserService Executed");

		return new User(user.getUsername(), user.getPassword(), true, true, true, true,
				getGrantedAuthorities(user));
	}*/

/*	public UserDetails loadUserByUsernameVal() throws UsernameNotFoundException {
		Users user = userAccountService.getUserByName(userNameParam);
		if (user == null) {
			throw new UsernameNotFoundException("Username or password not found");
		}

		log.debug("Load UserService Executed");

		return new User(user.getUsername(), user.getPassword(), true, true, true, true,
				getGrantedAuthorities(user));
	}*/

/*	private List<GrantedAuthority> getGrantedAuthorities(Users user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		log.debug("User Roles Executed");
		return authorities;
	}

	 @Autowired
    private UsersService usersService;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new ApplicationUsers(usersService.getByUsrName(s).orElseThrow(() -> new UsernameNotFoundException("Username Not Found")));
    }

	*/


    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)  throws UsernameNotFoundException {
                Users u = usersService.getUserByName(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                return u;
            }
        };
    }
}

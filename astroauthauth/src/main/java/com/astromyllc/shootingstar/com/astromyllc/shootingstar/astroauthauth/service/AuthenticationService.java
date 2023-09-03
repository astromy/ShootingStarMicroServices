package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Component
@Service("authenticationService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements UserDetailsService {

	@Autowired
	private UsersService userAccountService;
	private String userNameParam;
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userAccountService.getUserByName(username);
		if (user == null) {
			log.debug(username);
				throw new UsernameNotFoundException("Username or password not found");
		}

		log.debug("Load UserService Executed");

		return new User(user.getUserName(), user.getUserPassword(), true, true, true, true,
				getGrantedAuthorities(user));
	}

	public UserDetails loadUserByUsernameVal() throws UsernameNotFoundException {
		Users user = userAccountService.getUserByName(userNameParam);
		if (user == null) {
			throw new UsernameNotFoundException("Username or password not found");
		}

		log.debug("Load UserService Executed");

		return new User(user.getUserName(), user.getUserPassword(), true, true, true, true,
				getGrantedAuthorities(user));
	}

	private List<GrantedAuthority> getGrantedAuthorities(Users user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		log.debug("User Roles Executed");
		return authorities;
	}

}

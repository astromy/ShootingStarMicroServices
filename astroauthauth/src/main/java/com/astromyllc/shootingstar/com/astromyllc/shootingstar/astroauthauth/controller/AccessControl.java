package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.controller;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.request.UsersRequest;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.dto.response.JwtAuthenticationResponse;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.serviceInterface.UsersServiceInterface;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/api/astroauthauth/auth")
@RequiredArgsConstructor
@Slf4j
public class AccessControl {
private final UsersServiceInterface usersServiceInterface;

	/*@Autowired
	private PasswordEncoder encoder*/;

	@PostMapping("/signup")
	public ResponseEntity<JwtAuthenticationResponse> signup(@RequestBody UsersRequest request) {
		return ResponseEntity.ok(usersServiceInterface.signup(request));
	}

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody UsersRequest request) {
		return ResponseEntity.ok(usersServiceInterface.signin(request));
	}

/*	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String accessControl(Authentication auth, Model model, HttpSession session) {
		model.addAttribute("users", new Users());

		return "login";
	}*/

	/*@RequestMapping(value = { "/index" }, method = RequestMethod.GET)
	public String landing(Authentication auth, Model model) {
		String usersName = auth.getName();
	*//*	ShopUser su=shopUserRepository.findShopUserByUsername(usersName);
		if(su!=null){
		model.addAttribute("userpermission",
				su.getPermission().getPermissionType());
		}*//*
		return "index";
	}*/

/*	@RequestMapping(value = { "/index.html" }, method = RequestMethod.GET)
	public String index() {
		return "index";
	}*/

/*
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String searchPage(Authentication auth, Model model) {
		String usersName;
		try {

			usersName = auth.getName();
			Users su = usersServiceInterface.getUserByName(usersName);
		} catch (Exception e) {
			return (e.toString());
		}
		return usersName;
	}

	@ResponseBody
	@RequestMapping("/get_authentication")
	public String getUserRoleByUser(Authentication auth) {
		String username = "";
		username = auth.getName();
		return username;
	}
*/

}

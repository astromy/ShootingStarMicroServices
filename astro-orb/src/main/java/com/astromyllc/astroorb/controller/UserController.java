package com.astromyllc.astroorb.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping({"/","index","/home"})
    public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) {
        Authentication authentications = SecurityContextHolder.getContext().getAuthentication();

            // Extract the "groups" attribute
            List<String> realmRoles = (List<String>) principal.getAttributes().get("groups");

            // Load the authorized client
            OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);

            // Populate the model with user details
            model.addAttribute("accessToken",authorizedClient.getAccessToken().getTokenValue());
            model.addAttribute("userName", principal.getAttribute("preferred_username"));
            model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientId());
            model.addAttribute("scopes", authorizedClient.getAccessToken().getScopes());
            model.addAttribute("access", authorizedClient.getAccessToken().getTokenValue());
            model.addAttribute("institutions", principal.getAttribute("institution_group"));
           //cd4 is a code name for ream roles
            model.addAttribute("cd4", realmRoles);

            return "index";
    }


    @GetMapping({"preorder"})
    public String gethome(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) {

        return "login";

    }

   /* @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;*/

/*    @Value("${keycloak.realm}")
    private String realm;

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        String redirectUri = request.getRequestURL().toString().replace(request.getRequestURI(), "/");
        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout?redirect_uri=%s",
                keycloakBaseUrl, realm, redirectUri);
        return "redirect:" + logoutUrl;
    }*/


    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
    }

}

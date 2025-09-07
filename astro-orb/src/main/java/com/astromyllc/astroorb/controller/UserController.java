package com.astromyllc.astroorb.controller;


import com.astromyllc.astroorb.dto.request.SingleStringRequest;
import com.astromyllc.astroorb.dto.response.SkimpInstitutionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@Slf4j
public class UserController {

    @Value("${gateway.host}")
    private String backendserve;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping({"/", "index", "/home"})
    public String getIndex(Model model, @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken authentication) throws IOException, InterruptedException {
        Authentication authentications = SecurityContextHolder.getContext().getAuthentication();

        // Extract the "groups" attribute
        List<String> realmRoles = (List<String>) principal.getAttributes().get("groups");

        // Load the authorized client
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);

        // Populate the model with user details
        model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
        model.addAttribute("userName", principal.getAttribute("preferred_username"));
        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientId());
        model.addAttribute("scopes", authorizedClient.getAccessToken().getScopes());
        model.addAttribute("access", authorizedClient.getAccessToken().getTokenValue());
        model.addAttribute("institutions", principal.getAttribute("institution_group"));
        //cd4 is a code name for ream roles
        model.addAttribute("cd4", realmRoles);


        // Check activation status
        SingleStringRequest request = new SingleStringRequest();
        request.setVal(principal.getAttribute("institution_group").toString().split(",")[0].split("/")[1].replace("]","").trim());

        ResponseEntity<SkimpInstitutionResponse> response = checkActivationStatus(request);
        //String responseBody = response.getBody();

        if (response != null && Objects.requireNonNull(response.getBody()).getStatus().equalsIgnoreCase("Suspended")) {
            // Add institution ID to model for the suspended page
            model.addAttribute("institutionId", principal.getAttribute("institution_group").toString().split(",")[0].split("/")[1].replace("]","").trim());
            model.addAttribute("userName", principal.getAttribute("preferred_username"));
            model.addAttribute("bill", response.getBody().getPendingBill());
            model.addAttribute("email", response.getBody().getEmail());
            model.addAttribute("plan", response.getBody().getSubscription());
            model.addAttribute("population", response.getBody().getPopulation());
            return "suspended"; // Return suspended page template
        }


        return "index";
    }

    public ResponseEntity<SkimpInstitutionResponse> checkActivationStatus(SingleStringRequest jso) throws IOException, InterruptedException {

        // Minimal but crucial additions
        if (jso == null || jso.getVal() == null) {
            log.warn("Null request received");
            return ResponseEntity.badRequest().build();
        }

        try {
            ResponseEntity<String> rawResponse = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionStatus");

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
// Disable WRITE_DATES_AS_TIMESTAMPS to get ISO-8601 format
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            SkimpInstitutionResponse response = mapper.readValue(rawResponse.getBody(), SkimpInstitutionResponse.class);

            return ResponseEntity.status(rawResponse.getStatusCode())
                    .body(response);
        } catch (IOException e) {
            log.error("Backend call failed for {}", jso.getVal(), e);
            throw e;
        }
    }

    @GetMapping("/api/token")
    public Map<String, String> getToken(JwtAuthenticationToken authentication) {
        String tokenValue = authentication.getToken().getTokenValue();
        return Map.of("token", tokenValue);
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

    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {
        log.info("Calling API: {}", url);


        try {
            // 4. Create the request with proper authorization header
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(jso)))
                    .build();


            // 5. Execute and handle response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) {
                log.error("Backend rejected token. Status: {} - Body: {}", response.statusCode(), response.body());
            }

            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception e) {
            log.error("API call failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

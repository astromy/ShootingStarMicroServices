package com.astromyllc.astroorb.controller;


import com.astromyllc.astroorb.dto.request.SingleStringRequest;
import com.astromyllc.astroorb.dto.response.SkimpInstitutionResponse;
import com.astromyllc.astroorb.dto.response.StaffResponse;
import com.astromyllc.astroorb.subscription.InstitutionSubscriptionService;
import com.astromyllc.astroorb.subscription.SubscriptionPlan;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@Component
@RequiredArgsConstructor
public class UserController {
    private static final ObjectMapper JSON = new ObjectMapper().registerModule(new JavaTimeModule());

    @NonNull  // Add this to include in constructor
    @Qualifier("webClientBuilder")
    private final WebClient.Builder webClientBuilder;

    @NonNull  // Add this to include in constructor
    @Qualifier("directWebClientBuilder")
    private final WebClient.Builder directWebClientBuilder;
    @NonNull
    private final InstitutionSubscriptionService subscriptionService;
    @Value("${gateway.host}")
    private String backendserve;
    @Value("${paystack.public-key}")
    private String paystackPublicKey;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping({"/", "index", "/home"})
    public String getIndex(Model model,
                           @AuthenticationPrincipal OAuth2User principal,
                           HttpServletRequest httprequest,
                           OAuth2AuthenticationToken authentication) {
        model.addAttribute("paystackPublicKey", paystackPublicKey);
// Get CSRF token
        CsrfToken csrfToken = (CsrfToken) httprequest.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken.getToken());
            model.addAttribute("_csrf_header", csrfToken.getHeaderName());
        }

        try {
            // Safe to proceed - principal is not null
            List<String> realmRoles = (List<String>) principal.getAttributes().get("groups");
            if (realmRoles == null) {
                realmRoles = new ArrayList<>();
            }

            // Safely get authorized client
            OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authentication);
            if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                return "redirect:/oauth2/authorization/ShootingStar";
            }

            // Populate model with null checks
            model.addAttribute("access", authorizedClient.getAccessToken().getTokenValue());
            log.info("Access =" + authorizedClient.getAccessToken().getTokenValue());
            model.addAttribute("accessToken", authorizedClient.getAccessToken().getTokenValue());
            log.info("AccessToken =" + authorizedClient.getAccessToken().getTokenValue());
            model.addAttribute("userName", principal.getAttribute("preferred_username"));
            // model.addAttribute("userAvatar", getUser(principal.getAttribute("preferred_username")));

            String staffCode = principal.getAttribute("preferred_username");
            SingleStringRequest staffCodeRequest = new SingleStringRequest();
            staffCodeRequest.setVal(staffCode);
            ResponseEntity<StaffResponse> staffResponse = getUser(staffCodeRequest);
            String userAvatar = null;
            if (staffResponse.getStatusCode().is2xxSuccessful() && staffResponse.getBody() != null) {
                userAvatar = staffResponse.getBody().getStaffPicture();
            }
            model.addAttribute("userAvatar", userAvatar);

            model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientId());
            model.addAttribute("scopes", authorizedClient.getAccessToken().getScopes());
            model.addAttribute("institutions", principal.getAttribute("institution_group"));
            model.addAttribute("cd4", realmRoles);

            // Safely check institution status
            SubscriptionPlan currentPlan = SubscriptionPlan.STARTER;
            boolean planResolved = false;
            Object institutionObj = principal.getAttribute("institution_group");
            if (institutionObj != null) {
                String institutionGroup = institutionObj.toString();
                try {
                    String institutionCode = institutionGroup.split(",")[0].split("/")[1].replace("]", "").trim();

                    SingleStringRequest request = new SingleStringRequest();
                    request.setVal(institutionCode);

                    ResponseEntity<SkimpInstitutionResponse> response = checkActivationStatus(request);

                    if (response != null && response.getBody() != null &&
                            "Suspended".equalsIgnoreCase(response.getBody().getStatus())) {

                        model.addAttribute("institutionId", institutionCode);
                        model.addAttribute("bill", response.getBody().getPendingBill());
                        model.addAttribute("email", response.getBody().getEmail());
                        model.addAttribute("plan", response.getBody().getSubscription());
                        model.addAttribute("population", response.getBody().getPopulation());
                        return "suspended";
                    }

                    if (response != null && response.getBody() != null) {
                        // Reuse the subscription value from the status call above instead of
                        // making a second backend round-trip; this is the same parsing
                        // SubscriptionEnforcementInterceptor/InstitutionSubscriptionService use.
                        currentPlan = SubscriptionPlan.fromLabel(response.getBody().getSubscription());
                        planResolved = true;
                    }
                } catch (Exception e) {
                    log.error("Error parsing institution code", e);
                }
            }
            if (!planResolved) {
                // Status call above didn't yield a usable subscription value (missing
                // institution attribute, parse failure, backend error, etc). Falls back to
                // the same session-cached resolver the interceptor uses.
                currentPlan = subscriptionService.resolvePlan(httprequest.getSession(true), principal);
            }
            model.addAttribute("subscriptionPlan", currentPlan.name());

            return "index";

        } catch (Exception e) {
            log.error("Unexpected error in index page", e);
            // Don't show 500 to user - redirect to login instead
            return "redirect:/oauth2/authorization/ShootingStar";
        }
    }


    public ResponseEntity<SkimpInstitutionResponse> checkActivationStatus(SingleStringRequest jso) {
        if (jso == null || jso.getVal() == null) {
            log.warn("Null request received");
            return ResponseEntity.badRequest().build();
        }

        try {
            log.info("Calling backend service for institution: {}", jso.getVal());
            WebClient.Builder webClientBuilder0 = webClientBuilder;
            if (backendserve.contains("localhost")) {
                webClientBuilder0 = directWebClientBuilder;
            }
            SkimpInstitutionResponse responseBody =
                    webClientBuilder0
                            .baseUrl(backendserve)
                            .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                                log.info("=== REQUEST DEBUG ===");
                                log.info("Method: {}", clientRequest.method());
                                log.info("URL: {}", clientRequest.url());
                                log.info("Headers:");
                                clientRequest.headers().forEach((name, values) -> {
                                    values.forEach(value -> {
                                        // Mask Authorization headers if they exist
                                        if ("Authorization".equalsIgnoreCase(name)) {
                                            log.info("  {}: [HIDDEN - LENGTH: {}]", name, value.length());
                                        } else {
                                            log.info("  {}: {}", name, value);
                                        }
                                    });
                                });
                                log.info("=== END REQUEST DEBUG ===");
                                return Mono.just(clientRequest);
                            }))
                            .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                                log.info("=== RESPONSE DEBUG ===");
                                log.info("Status: {}", clientResponse.statusCode());
                                log.info("Response Headers:");
                                clientResponse.headers().asHttpHeaders().forEach((name, values) -> {
                                    values.forEach(value -> log.info("  {}: {}", name, value));
                                });
                                log.info("=== END RESPONSE DEBUG ===");
                                return Mono.just(clientResponse);
                            }))
                            .build()
                            .post()
                            .uri("/api/setup/getInstitutionStatus")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .bodyValue(jso)
                            .retrieve()
                            .bodyToMono(SkimpInstitutionResponse.class)
                            .block();

            log.info("Backend call successful for institution: {}", jso.getVal());
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("Backend call failed for {}", jso.getVal(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    private SkimpInstitutionResponse createDefaultResponse() {
        SkimpInstitutionResponse defaultResponse = new SkimpInstitutionResponse();
        defaultResponse.setStatus("Active"); // Assume active if backend fails
        defaultResponse.setPendingBill(0.0);
        defaultResponse.setEmail("unknown@example.com");
        defaultResponse.setSubscription("Starter");
        defaultResponse.setPopulation(0L);
        return defaultResponse;
    }

    private ResponseEntity<StaffResponse> getUser(@RequestBody SingleStringRequest staffCode) {
        ResponseEntity<StaffResponse> response = BACKENDCOMMPOST(staffCode, backendserve + "/api/hr/getStaffByStaffId", StaffResponse.class);
        return response;
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

    @GetMapping("/upgrade-required")
    public String upgradeRequired(Model model,
                                  @AuthenticationPrincipal OAuth2User principal,
                                  HttpServletRequest httprequest,
                                  @RequestParam(required = false) String required,
                                  @RequestParam(required = false) String current) {
        CsrfToken csrfToken = (CsrfToken) httprequest.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken.getToken());
            model.addAttribute("_csrf_header", csrfToken.getHeaderName());
        }
        model.addAttribute("paystackPublicKey", paystackPublicKey);
        model.addAttribute("requiredPlan", required != null ? required : "a higher");
        model.addAttribute("currentPlan", current != null ? current : "your current");

        String institutionCode = null;
        String email = null;
        Object institutionObj = principal != null ? principal.getAttribute("institution_group") : null;
        if (institutionObj != null) {
            try {
                institutionCode = institutionObj.toString().split(",")[0].split("/")[1].replace("]", "").trim();

                SingleStringRequest request = new SingleStringRequest();
                request.setVal(institutionCode);
                ResponseEntity<SkimpInstitutionResponse> response = checkActivationStatus(request);
                if (response != null && response.getBody() != null) {
                    email = response.getBody().getEmail();
                }
            } catch (Exception e) {
                log.error("Error resolving institution details for upgrade page", e);
            }
        }
        // institutionCode/email may be null if resolution above failed - the page's JS
        // checks for that and disables the "Pay Now" flow rather than erroring, since
        // this is a display page, not something that should 500 on a parsing hiccup.
        model.addAttribute("institutionId", institutionCode);
        model.addAttribute("email", email);

        return "upgrade-required";
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


   /* private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        return this.authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
    }*/

    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
        if (authentication == null || authorizedClientService == null) {
            return null;
        }

        try {
            return authorizedClientService.loadAuthorizedClient(
                    authentication.getAuthorizedClientRegistrationId(),
                    authentication.getName()
            );
        } catch (Exception e) {
            log.error("Error loading authorized client", e);
            return null;
        }
    }

    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {
        log.info("Calling API: {}", url);


        try {
            // 4. Create the request with proper authorization header
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(jso)))
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

    private <T> ResponseEntity<T> BACKENDCOMMPOST(Object jso, String url, Class<T> responseType) {
        log.info("Calling API: {}", url);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(jso)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) {
                log.error("Backend rejected token. Status: {} - Body: {}", response.statusCode(), response.body());
            }

            boolean success = response.statusCode() >= 200 && response.statusCode() < 300;
            if (success && response.body() != null && !response.body().isBlank()) {
                try {
                    T body = JSON.readValue(response.body(), responseType);
                    return ResponseEntity.status(response.statusCode()).body(body);
                } catch (Exception parseEx) {
                    log.error("Response from {} could not be parsed as {}: {}", url, responseType.getSimpleName(), parseEx.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            return ResponseEntity.status(response.statusCode()).build();
        } catch (Exception e) {
            log.error("API call failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

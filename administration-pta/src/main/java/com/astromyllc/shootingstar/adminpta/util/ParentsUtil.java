package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.ParentsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.ParentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParentsUtil {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static List<Parents> parentGlobalList;
    public static InstitutionRequest institutionRequest = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //private static String keycloakURL;
    private final WebClient.Builder webClientBuilder;
    private final ParentRepository parentRepository;
    private final MailUtil mailUtil;
    AtomicBoolean isNewUser = new AtomicBoolean(false);
    @Value("${gateway.host}")
    private String host;
    @Value("${keycloak.address}")
    private String staticKeycloakURL;
    private String generatedPass = null;

    public static Parents mapParentRequest_ToParent(ParentsRequest r, String studentId) {
        return Parents.builder()
                .parentType(r.getParentType())
                .email(r.getEmail())
                .lastName(r.getLastName())
                .institutionCode(r.getInstitutionCode())
                .occupation(r.getOccupation())
                .placeOfWork(r.getPlaceOfWork())
                .firstNames(r.getFirstNames())
                .contact1(r.getContact1())
                .contact2(r.getContact2())
                .studentId(studentId)
                .build();
    }

    public static ParentsResponse mapParents_ToParentsResponse(Parents parents) {
        return ParentsResponse.builder()
                .parentType(parents.getParentType())
                .email(parents.getEmail())
                .lastName(parents.getLastName())
                .institutionCode(parents.getInstitutionCode())
                .occupation(parents.getOccupation())
                .placeOfWork(parents.getPlaceOfWork())
                .firstNames(parents.getFirstNames())
                .contact1(parents.getContact1())
                .contact2(parents.getContact2())
                .studentId(parents.getStudentId())
                .build();
    }

    /*@PostConstruct
    private void initStaticKeycloakURL() {
        keycloakURL = staticKeycloakURL;
    }*/

    public InstitutionRequest getInstitution(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();
        return institutionRequest =

                webClientBuilder
                        .baseUrl("http://" + host)
                        .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            System.out.println("Request: " + clientRequest);
                            return Mono.just(clientRequest);
                        }))
                        .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                            System.out.println("Response: " + clientResponse);
                            return Mono.just(clientResponse);
                        }))
                        .build()
                        .post()
                        .uri("/api/setup/getInstitutionByCode")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(InstitutionRequest.class)
                        .block();

    }

    public InstitutionRequest getSkimpInstitution(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();
        return institutionRequest =

                webClientBuilder
                        .baseUrl("http://" + host)
                        .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            System.out.println("Request: " + clientRequest);
                            return Mono.just(clientRequest);
                        }))
                        .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                            System.out.println("Response: " + clientResponse);
                            return Mono.just(clientResponse);
                        }))
                        .build()
                        .post()
                        .uri("/api/setup/getInstitutionStatus")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(InstitutionRequest.class)
                        .block();

    }

    public void updateParents(Parents p, ParentsRequest pr, String studId) {
        p.setParentType(pr.getParentType());
        p.setEmail(pr.getEmail());
        p.setContact1(pr.getContact1());
        p.setContact2(pr.getContact2());
        p.setFirstNames(pr.getFirstNames());
        p.setLastName(pr.getLastName());
        p.setOccupation(pr.getOccupation());
        p.setPlaceOfWork(pr.getPlaceOfWork());
        p.setInstitutionCode(pr.getInstitutionCode());
    }

    public void saveAll(List<Parents> p) {
        parentRepository.saveAll(p);
        parentGlobalList.addAll(p);
    }

    @PostConstruct
    private void fetAllParents() throws ExecutionException, InterruptedException {
        parentGlobalList = parentRepository.findAll();
        log.info("Global Parents List populated with {} records", parentGlobalList.size());
       /* log.info("Started Working On Parents Logins");
        bulkCreateKeycloakUsers(parentGlobalList.stream().map(this::mapParent_ToParentRequest).toList());

        log.info("Completed Working On Parents Logins");*/
    }

    public boolean sendmail(String recipient, String mailBody) {
        try {
            return mailUtil.sendTransactionalEmail(
                    recipient,
                    "Reactivation of Account",
                    mailBody,
                    "support"
            );
        } catch (Exception e) {
            log.error("Failed to send reactivation email to {}", recipient, e);
            return false;
        }
    }

    public ParentsRequest mapParent_ToParentRequest(Parents p) {
        return ParentsRequest.builder()
                .studentId(p.getStudentId())
                .institutionCode(p.getInstitutionCode())
                .parentType(p.getParentType())
                .firstNames(p.getFirstNames())
                .lastName(p.getLastName())
                .contact1(p.getContact1())
                .contact2(p.getContact2())
                .email(p.getEmail())
                .occupation(p.getOccupation())
                .placeOfWork(p.getPlaceOfWork())
                .build();
    }


    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    /*
    public void KeyclaokCreateUserCredentials(ParentsRequest parentsRequests) {
        if (parentsRequests == null || parentsRequests.getContact1() == null) {
            throw new IllegalArgumentException("Parent requests cannot be empty");
        }

        ParentsRequest request = parentsRequests;

        // Find staff email from global list
        String parentContact = parentGlobalList.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Parent not found: " + request.getStudentId()))
                .getContact1();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(staticKeycloakURL)
                .realm("ShootingStar")
                .clientId("astro_orb_microservices")
                .clientSecret("VSOLTuWTCIasuAK5xNS93YOKfmnwtUlE")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        try {
            RealmResource realmResource = keycloak.realm("ShootingStar");
            UsersResource usersResource = realmResource.users();

            // 1. Find or create user
            UserRepresentation user = findOrCreateUser(usersResource, parentContact, request.getEmail());
            String userId = user.getId();

            List<GroupRepresentation> groups = realmResource.groups().groups();
            GroupRepresentation tGroup = groups.stream()
                    .filter(g -> g.getName().equalsIgnoreCase("parents"))
                    .findFirst()
                    .orElse(null);


            List<GroupRepresentation> userGroups = keycloak.realm("ShootingStar")
                    .users()
                    .get(userId)
                    .groups();


            // Check if user is already in the group
            boolean alreadyInGroup = userGroups.stream()
                    .anyMatch(group -> group.getId().equals(tGroup.getId()));

            if (!alreadyInGroup) {
                keycloak.realm("ShootingStar").users().get(userId).joinGroup(tGroup.getId());
                log.info("Successfully processed parent for user: {}", request.getStudentId());
            }


            generatedPass = passwordGen();
            String mailBody = "<html><body>" +
                    "Please find herein, your credentials to the Orb School Application.<br>" +
                    "Username: " + request.getContact1() + "<br>" +
                    "Password: " + generatedPass + "<br>" +
                    "Link to the app: <a href='https://orb.astromyllc.com'>https://orb.astromyllc.com</a>" +
                    "</body></html>";
            //"Please find herein, your credentials to the Orb School Application.\n\nUsername: " + staffCode + "\n\nPassword: " + staffCode + "!23\n\nLink to the app: https://orb.astromyllc.com";

            // 2. Handle password for new users
            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                if (isNewUser.get()) {
                    setInitialPassword(usersResource, request.getContact1());
                    // Send email asynchronously
                    CompletableFuture.runAsync(() -> {
                        try {
                            String fromEmail = institutionRequest.getName().replace(" ", ".");
                            int secondDotIndex = fromEmail.indexOf('.', fromEmail.indexOf('.') + 1);
                            fromEmail = fromEmail.substring(0, secondDotIndex);
                            mailUtil.sendTransactionalEmail(parentContact, "User Credentials to the ORB application", mailBody, fromEmail);
                        } catch (Exception e) {
                            log.error("Failed to send email to {}", parentContact, e);
                        }
                    });
                }
                {
                    String fromEmail = getInstitution(request.getInstitutionCode()).getName().replace(" ", ".");
                    int secondDotIndex = fromEmail.indexOf('.', fromEmail.indexOf('.') + 1);
                    fromEmail = fromEmail.substring(0, secondDotIndex);
                    mailUtil.sendTransactionalEmail(parentContact, "User Credentials to the ORB application", mailBody, fromEmail);
                }
                log.info("Successfully processed parent for user: {}", request.getStudentId());
            }


        } catch (Exception e) {
            log.error("Failed to process Keycloak Parent user credentials for {}", request.getStudentId(), e);
            throw new RuntimeException("User creation/update failed for " + request.getStudentId(), e);
        } finally {
            keycloak.close();
        }
    }*/

    public void bulkCreateKeycloakUsers(List<ParentsRequest> parentRequests) throws ExecutionException, InterruptedException {
        if (parentRequests == null || parentRequests.isEmpty()) {
            throw new IllegalArgumentException("Parent requests cannot be empty");
        }

        // 1. Configure custom ForkJoinPool with limited parallelism
        ForkJoinPool customPool = new ForkJoinPool(3); // Max 3 concurrent threads

        try {
            // 2. Process batches with controlled parallelism
            customPool.submit(() ->
                    Lists.partition(parentRequests, 20) // Smaller batches
                            .parallelStream()
                            .forEach(batch -> {
                                try (Keycloak keycloak = buildKeycloakClient()) {
                                    processBatch(batch, keycloak);
                                    Thread.sleep(200); // Small delay
                                } catch (Exception e) {
                                    log.error("Batch failed", e);
                                }
                            })
            ).get(); // Wait for completion

        } finally {
            customPool.shutdown();
        }
    }

    private Keycloak buildKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(staticKeycloakURL)
                .realm("ShootingStar")
                .clientId("astro_orb_microservices")
                .clientSecret("VSOLTuWTCIasuAK5xNS93YOKfmnwtUlE")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    private void processBatch(List<ParentsRequest> batch, Keycloak keycloak) {
        RealmResource realm = keycloak.realm("ShootingStar");
        UsersResource users = realm.users();
        GroupRepresentation group = getOrCreateParentGroup(realm);

        batch.forEach(request -> {
            try {
                processSingleUser(request, users, group);
            } catch (Exception e) {
                log.error("User {} failed", request.getStudentId(), e);
            }
        });
    }

    private void processSingleUser(ParentsRequest request, UsersResource usersResource,
                                   GroupRepresentation parentGroup) {
        try {
            String parentContact = parentGlobalList.stream()
                    .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Parent not found: " + request.getStudentId()))
                    .getContact1();

            UserRepresentation user = findOrCreateUser(usersResource, parentContact, request.getEmail());

            // Handle group membership
            addUserToGroup(usersResource, user.getId(), parentGroup);

            // Handle password and email if new user
            if (request.getEmail() != null && !request.getEmail().isBlank() && isNewUser.get()) {
                setInitialPassword(usersResource, request.getContact1());
                sendCredentialsEmail(request, parentContact);
            }

            log.info("Successfully processed parent for user: {}", request.getStudentId());

        } catch (Exception e) {
            log.error("Failed to process user {}", request.getStudentId(), e);
            // Consider adding to retry queue instead of throwing
        }
    }

    private GroupRepresentation getOrCreateParentGroup(RealmResource realmResource) {
        return realmResource.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase("parents"))
                .findFirst()
                .orElseGet(() -> {
                    GroupRepresentation newGroup = new GroupRepresentation();
                    newGroup.setName("parents");
                    realmResource.groups().add(newGroup);
                    return newGroup;
                });
    }

    private void addUserToGroup(UsersResource usersResource, String userId,
                                GroupRepresentation group) {
        boolean alreadyInGroup = usersResource.get(userId).groups().stream()
                .anyMatch(g -> g.getId().equals(group.getId()));

        if (!alreadyInGroup) {
            usersResource.get(userId).joinGroup(group.getId());
        }
    }

    private void sendCredentialsEmail(ParentsRequest request, String email) {
        String mailBody = "<html><body>" +
                "Please find herein, your credentials to the Orb School Application.<br>" +
                "Username: " + request.getContact1() + "<br>" +
                "Password: " + generatedPass + "<br>" +
                "Link to the app: <a href='https://orb.astromyllc.com'>https://orb.astromyllc.com</a>" +
                "</body></html>";

        CompletableFuture.runAsync(() -> {
            try {
                String fromEmail = getInstitution(request.getInstitutionCode()).getName()
                        .replace(" ", ".");
                fromEmail = fromEmail.substring(0, fromEmail.indexOf('.', fromEmail.indexOf('.') + 1));
                mailUtil.sendTransactionalEmail(email, "User Credentials to the ORB application",
                        mailBody, fromEmail);
            } catch (Exception e) {
                log.error("Failed to send email to {}", email, e);
            }
        });
    }

    private UserRepresentation findOrCreateUser(UsersResource usersResource, String parentContact, String parentEmail) {
        // Try to find existing user first
        List<UserRepresentation> users = usersResource.search(parentContact, true);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        // Create new user if not found
        UserRepresentation newUser = new UserRepresentation();
        if (parentEmail != null && !parentEmail.isBlank() && !isValidEmail(parentEmail)) {
            newUser.setUsername(parentContact);
            newUser.setEmail(parentEmail);
            newUser.setEnabled(true);
        } else {
            newUser.setUsername(parentContact);
            newUser.setEnabled(true);
        }


        Response response = usersResource.create(newUser);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
        }

        // Get the created user with retry logic
        return waitForUserCreation(usersResource, parentContact);
    }

    private UserRepresentation waitForUserCreation(UsersResource usersResource, String parentContact) {
        int attempts = 0;
        while (attempts < 3) {
            List<UserRepresentation> users = usersResource.search(parentContact);
            if (!users.isEmpty()) {
                isNewUser.set(true); // Mark user as newly created
                return users.get(0);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for user creation");
            }
            attempts++;
        }
        throw new RuntimeException("User creation verification failed after retries");
    }

    private void setInitialPassword(UsersResource usersResource, String parentContact) {
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(true);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(generatedPass);

        usersResource.get(parentContact).resetPassword(password);
        log.info("Password set successfully for user: {}", parentContact);
    }

    private RoleRepresentation getRoleRepresentation(RealmResource realmResource, String roleName) {
        try {
            return realmResource.roles().get(roleName).toRepresentation();
        } catch (Exception e) {
            log.warn("Role {} not found in Keycloak", roleName);
            return null;
        }
    }

    public GroupRepresentation searchGroupsByName(Keycloak keycloak, String realm, String searchTerm) {
        return keycloak.realm(realm)
                .groups()
                .groups()
                .stream()
                .filter(group -> group.getName().equals(searchTerm))
                .findFirst()
                .orElse(null);
    }

    private String passwordGen() {

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARS.length());
            password.append(CHARS.charAt(index));
        }

        return password.toString();
    }
}

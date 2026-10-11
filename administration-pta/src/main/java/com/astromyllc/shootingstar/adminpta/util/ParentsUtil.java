package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.ParentsRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.ParentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParentsUtil {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final String REALM = "ShootingStar";
    private static final String CLIENT_ID = "astro_orb_microservices";
    // TODO: move to configuration and rotate - a secret should not live in source code.
    private static final String CLIENT_SECRET = "VSOLTuWTCIasuAK5xNS93YOKfmnwtUlE";
    private static final String PARENTS_GROUP = "parents";
    private static final String CREDENTIALS_SUBJECT = "User Credentials to the ORB application";

    public static List<Parents> parentGlobalList;
    public static InstitutionRequest institutionRequest = null;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ---- Institution cache (Tier 1 fix) ----
    // 10,000 institutions x ~1KB each is trivial memory; expiring after 24h
    // is fine because institution metadata (name, bececode) rarely changes.
    // If your org needs faster propagation, drop to 1h - the win is still huge.
    private final Cache<String, InstitutionRequest> institutionCache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofHours(24))
                    .maximumSize(10_000)
                    .recordStats()
                    .build();

    private final WebClient.Builder webClientBuilder;
    private final ParentRepository parentRepository;
    private final MailUtil mailUtil;
    AtomicBoolean isNewUser = new AtomicBoolean(false);
    // Built once at startup. Building a client from the shared builder on every call is not
    // thread-safe and fails with ConcurrentModificationException under concurrent requests.
    private WebClient gatewayClient;
    @Value("${gateway.host}")
    private String host;

    @Value("${keycloak.address}")
    private String staticKeycloakURL;

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
                .id(String.valueOf(parents.getId()))
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

    /**
     * Sender name taken from the institution name: its first two words joined by a dot,
     * e.g. "Bright Future Academy" -> "Bright.Future". Shorter names are used whole.
     */
    private static String deriveFromEmail(String institutionName) {
        String dotted = institutionName.replace(" ", ".");
        int secondDotIndex = dotted.indexOf('.', dotted.indexOf('.') + 1);
        return secondDotIndex > 0 ? dotted.substring(0, secondDotIndex) : dotted;
    }

    // =============================================================
    //  Institution lookups - cached + batched
    // =============================================================

    @PostConstruct
    private void initGatewayClient() {
        gatewayClient = webClientBuilder.clone().baseUrl(host).build();
    }

    /**
     * Full institution record (used for email "from" address derivation).
     * Cached aggressively - institution metadata never changes at runtime.
     */
    public InstitutionRequest getInstitution(String institutionCode) {
        if (institutionCode == null || institutionCode.isBlank()) return null;
        return institutionCache.get(institutionCode, this::fetchInstitutionByCode);
    }

    /**
     * Skim institution record (name + bececode only).
     * Delegates to the same cache as getInstitution because both endpoints
     * return the same underlying entity - if they diverge in your setup
     * service, split into two caches.
     */
    public InstitutionRequest getSkimpInstitution(String institutionCode) {
        if (institutionCode == null || institutionCode.isBlank()) return null;
        return institutionCache.get(institutionCode, this::fetchInstitutionStatus);
    }

    /**
     * Batch variant - resolves many institution codes with at most one HTTP
     * call per uncached code, and parallelises the misses. Use this from
     * any code that maps over a collection of students.
     * <p>
     * Returns a map keyed by institution code. Missing/unresolvable codes are
     * simply absent from the map (callers should null-check the lookup).
     */
    public Map<String, InstitutionRequest> getInstitutionsBulk(Set<String> institutionCodes) {
        Map<String, InstitutionRequest> result = new HashMap<>();
        if (institutionCodes == null || institutionCodes.isEmpty()) return result;

        // 1. Serve cached entries
        List<String> misses = new ArrayList<>();
        for (String code : institutionCodes) {
            if (code == null || code.isBlank()) continue;
            InstitutionRequest cached = institutionCache.getIfPresent(code);
            if (cached != null) {
                result.put(code, cached);
            } else {
                misses.add(code);
            }
        }

        // 2. Fetch misses in parallel (bounded, non-blocking on the caller thread)
        if (!misses.isEmpty()) {
            Map<String, InstitutionRequest> fetched = Flux.fromIterable(misses)
                    .flatMap(code -> Mono.fromCallable(() -> fetchInstitutionStatus(code))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .doOnNext(r -> institutionCache.put(code, r))
                                    .onErrorResume(ex -> {
                                        log.warn("Failed to fetch institution {}: {}", code, ex.getMessage());
                                        return Mono.empty();
                                    })
                                    .map(r -> Map.entry(code, r)),
                            // Max parallel calls - tune to your setup service's capacity (8-16 is safe)
                            8)
                    .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                    .block(Duration.ofSeconds(15));

            if (fetched != null) result.putAll(fetched);
        }

        return result;
    }

    /**
     * Cold-path fetch for the full record.
     */
    private InstitutionRequest fetchInstitutionByCode(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();

        try {
            InstitutionRequest response = gatewayClient
                    .post()
                    .uri("/api/setup/getInstitutionByCode")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(InstitutionRequest.class)
                    .block(Duration.ofSeconds(5));   // hard cap - fail fast

            institutionRequest = response;
            return response;
        } catch (Exception e) {
            log.warn("getInstitutionByCode failed for {}: {}", institutionCode, e.getMessage());
            return null;
        }
    }

    // =============================================================
    //  Parents CRUD
    // =============================================================

    /**
     * Cold-path fetch for the skim record.
     */
    private InstitutionRequest fetchInstitutionStatus(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();

        try {
            InstitutionRequest response = gatewayClient
                    .post()
                    .uri("/api/setup/getInstitutionStatus")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(InstitutionRequest.class)
                    .block(Duration.ofSeconds(5));   // hard cap - fail fast

            institutionRequest = response;
            return response;
        } catch (Exception e) {
            log.warn("getInstitutionStatus failed for {}: {}", institutionCode, e.getMessage());
            return null;
        }
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
    private void fetAllParents() {
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

    // =============================================================
    //  Keycloak user provisioning
    // =============================================================

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public void KeyclaokCreateUserCredentials(ParentsRequest parentsRequests) {
        if (parentsRequests == null || parentsRequests.getContact1() == null) {
            throw new IllegalArgumentException("Parent requests cannot be empty");
        }

        ParentsRequest request = parentsRequests;

        // Find parent from global list or create if doesn't exist
        Parents existingParent = parentGlobalList.stream()
                .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId())
                        && s.getId().equals(request.getId()))
                .findFirst()
                .orElse(null);

        if (existingParent == null) {
            existingParent = mapParentRequest_ToParent(request, request.getStudentId());
            parentRepository.save(existingParent);
            parentGlobalList.add(existingParent);
            log.info("Created new parent from payload for student: {}", request.getStudentId());
        }

        String parentContact = existingParent.getContact1();

        try (Keycloak keycloak = buildKeycloakClient()) {
            RealmResource realmResource = keycloak.realm(REALM);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = findOrCreateUser(usersResource, parentContact, request.getEmail());
            String userId = user.getId();

            addUserToGroup(usersResource, userId, getOrCreateParentGroup(realmResource));

            if (request.getEmail() != null && !request.getEmail().isBlank()) {
                String password = passwordGen();
                if (isNewUser.get()) {
                    setInitialPassword(usersResource, userId, password);
                }

                String mailBody = buildCredentialsMailBody(request.getContact1(), password);

                // Send email asynchronously with parent's email address
                CompletableFuture.runAsync(() -> {
                    try {
                        InstitutionRequest inst = getInstitution(request.getInstitutionCode());
                        if (inst == null || inst.getName() == null) {
                            log.warn("Skipping credentials email - institution lookup failed for {}",
                                    request.getInstitutionCode());
                            return;
                        }
                        mailUtil.sendTransactionalEmail(
                                request.getEmail(),
                                CREDENTIALS_SUBJECT,
                                mailBody,
                                deriveFromEmail(inst.getName()),
                                true
                        );
                    } catch (Exception e) {
                        log.error("Failed to send email to {}", request.getEmail(), e);
                    }
                });
            }

            log.info("Successfully processed parent for user: {}", request.getStudentId());

        } catch (Exception e) {
            log.error("Failed to process Keycloak Parent user credentials for {}", request.getStudentId(), e);
            throw new RuntimeException("User creation/update failed for " + request.getStudentId(), e);
        }
    }

    public void bulkCreateKeycloakUsers(List<ParentsRequest> parentRequests) throws ExecutionException, InterruptedException {
        if (parentRequests == null || parentRequests.isEmpty()) {
            throw new IllegalArgumentException("Parent requests cannot be empty");
        }

        ForkJoinPool customPool = new ForkJoinPool(3);

        try {
            customPool.submit(() ->
                    Lists.partition(parentRequests, 20)
                            .parallelStream()
                            .forEach(batch -> {
                                try (Keycloak keycloak = buildKeycloakClient()) {
                                    processBatch(batch, keycloak);
                                    Thread.sleep(200);
                                } catch (Exception e) {
                                    log.error("Batch failed", e);
                                }
                            })
            ).get();

        } finally {
            customPool.shutdown();
        }
    }

    private Keycloak buildKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(staticKeycloakURL)
                .realm(REALM)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    private void processBatch(List<ParentsRequest> batch, Keycloak keycloak) {
        RealmResource realm = keycloak.realm(REALM);
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

    private void processSingleUser(ParentsRequest request,
                                   UsersResource usersResource,
                                   GroupRepresentation parentGroup) {
        try {
            String parentContact = parentGlobalList.stream()
                    .filter(s -> s.getStudentId().equalsIgnoreCase(request.getStudentId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Parent not found: " + request.getStudentId()))
                    .getContact1();

            UserRepresentation user = findOrCreateUser(usersResource, parentContact, request.getEmail());

            addUserToGroup(usersResource, user.getId(), parentGroup);

            if (request.getEmail() != null && !request.getEmail().isBlank() && isNewUser.get()) {
                String password = passwordGen();
                setInitialPassword(usersResource, user.getId(), password);
                sendCredentialsEmail(request, password);
            }

            log.info("Successfully processed parent for user: {}", request.getStudentId());

        } catch (Exception e) {
            log.error("Failed to process user {}", request.getStudentId(), e);
        }
    }

    private Optional<GroupRepresentation> findParentGroup(RealmResource realmResource) {
        return realmResource.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(PARENTS_GROUP))
                .findFirst();
    }

    private GroupRepresentation getOrCreateParentGroup(RealmResource realmResource) {
        return findParentGroup(realmResource).orElseGet(() -> {
            GroupRepresentation newGroup = new GroupRepresentation();
            newGroup.setName(PARENTS_GROUP);
            try (Response response = realmResource.groups().add(newGroup)) {
                if (response.getStatus() != 201) {
                    throw new RuntimeException("Failed to create the parents group in Keycloak. Status: "
                            + response.getStatus());
                }
            }
            // Re-read so the returned group carries the id Keycloak assigned
            return findParentGroup(realmResource)
                    .orElseThrow(() -> new RuntimeException("Parents group not found after creation"));
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

    private String buildCredentialsMailBody(String username, String password) {
        return "<html><body>" +
                "Please find herein, your credentials to the Orb School Application.<br>" +
                "Username: " + username + "<br>" +
                "Password: " + password + "<br>" +
                "Link to the app: <a href='https://orb.astromyllc.com'>https://orb.astromyllc.com</a>" +
                "</body></html>";
    }

    private void sendCredentialsEmail(ParentsRequest request, String password) {
        String mailBody = buildCredentialsMailBody(request.getContact1(), password);

        CompletableFuture.runAsync(() -> {
            try {
                InstitutionRequest inst = getInstitution(request.getInstitutionCode());
                if (inst == null || inst.getName() == null) {
                    log.warn("Skipping credentials email - institution lookup failed for {}",
                            request.getInstitutionCode());
                    return;
                }
                mailUtil.sendTransactionalEmail(
                        request.getEmail(),
                        CREDENTIALS_SUBJECT,
                        mailBody,
                        deriveFromEmail(inst.getName())
                );
            } catch (Exception e) {
                log.error("Failed to send email to {}", request.getEmail(), e);
            }
        });
    }

    private UserRepresentation findOrCreateUser(UsersResource usersResource,
                                                String parentContact,
                                                String parentEmail) {
        List<UserRepresentation> users = usersResource.search(parentContact, true);
        if (!users.isEmpty()) {
            return users.get(0);
        }

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(parentContact);
        newUser.setEnabled(true);
        // TODO: this condition looks inverted - it sets the email only when it is NOT valid.
        // Left unchanged on purpose; see the review notes before flipping it.
        if (parentEmail != null && !parentEmail.isBlank() && !isValidEmail(parentEmail)) {
            newUser.setEmail(parentEmail);
        }

        try (Response response = usersResource.create(newUser)) {
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
            }
        }

        return waitForUserCreation(usersResource, parentContact);
    }

    private UserRepresentation waitForUserCreation(UsersResource usersResource, String parentContact) {
        int attempts = 0;
        while (attempts < 3) {
            List<UserRepresentation> users = usersResource.search(parentContact, true);
            if (!users.isEmpty()) {
                isNewUser.set(true);
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

    private void setInitialPassword(UsersResource usersResource, String userId, String rawPassword) {
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(true);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(rawPassword);

        usersResource.get(userId).resetPassword(password);
        log.info("Password set successfully for user: {}", userId);
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
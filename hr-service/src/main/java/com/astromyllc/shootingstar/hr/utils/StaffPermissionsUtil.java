package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.model.StaffPermissions;
import com.astromyllc.shootingstar.hr.repository.StaffPermissionsRepository;
import jakarta.transaction.Transactional;
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
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffPermissionsUtil {
    @Value("${gateway.host}")
    private String host;
    @Value("${keycloak.address}")
    private String staticKeycloakURL;
    private static String keycloakURL;

    @PostConstruct
    private void initStaticKeycloakURL() {
        keycloakURL=staticKeycloakURL;
    }

    private final StaffPermissionsRepository staffPermissionsRepository;
    public static List<StaffPermissions> staffPermissionsGlobalList;
    public final MailUtil mailUtil;
    public final StaffUtil staffUtil;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private String generatedPass=null;

    @PostConstruct
    private void fetchAllPermissions() {

        staffPermissionsGlobalList = staffPermissionsRepository.findAll();
        log.info("{} RECORDS OF Staff Permissions FETCHED", staffPermissionsGlobalList.size());
    }

    public void saveAll(List<StaffPermissions> sp) {
        staffPermissionsRepository.saveAll(sp);
        staffPermissionsGlobalList.addAll(sp);
    }

    public static void updateStaffPermissions(StaffPermissions existing, StaffPermissionsRequest requestRecord) {
        existing.setPermission(requestRecord.getPermission());
        existing.setStaffCode(requestRecord.getStaffCode());
        existing.setPermissionCode(requestRecord.getPermissionCode());
        existing.setInstitutionCode(requestRecord.getInstitutionCode());
        existing.setState(requestRecord.getState());
    }

    public static StaffPermissions mapStaffPermissionsRequest_ToStaffPermissions(StaffPermissionsRequest requestRecord, String staffCode) {
        return StaffPermissions.builder()
                .permissionCode(requestRecord.getPermissionCode())
                .permission(requestRecord.getPermission())
                .staffCode(requestRecord.getStaffCode())
                .institutionCode(requestRecord.getInstitutionCode())
                .build();
    }

    AtomicBoolean isNewUser = new AtomicBoolean(false);
    public void KeyclaokCreateUserCredentials(List<StaffPermissionsRequest> staffPermissionsRequests) {
        if (staffPermissionsRequests == null || staffPermissionsRequests.isEmpty()) {
            throw new IllegalArgumentException("Staff permissions requests cannot be empty");
        }

        StaffPermissionsRequest request = staffPermissionsRequests.get(0);
        String staffCode = request.getStaffCode();

        // Find staff email from global list
        String staffEmail = StaffUtil.staffGlobalList.stream()
                .filter(s -> s.getStaffCode().equalsIgnoreCase(staffCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Staff not found: " + staffCode))
                .getStaffEmail();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakURL)
                .realm("ShootingStar")
                .clientId("astro_orb_microservices")
                .clientSecret("VSOLTuWTCIasuAK5xNS93YOKfmnwtUlE")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        try {
            RealmResource realmResource = keycloak.realm("ShootingStar");
            UsersResource usersResource = realmResource.users();

            // 1. Find or create user
            UserRepresentation user = findOrCreateUser(usersResource, staffCode, staffEmail);
            String userId = user.getId();

            List<GroupRepresentation> groups = realmResource.groups().groups();
            GroupRepresentation tGroup = groups.stream()
                    .filter(g -> g.getName().equalsIgnoreCase(request.getInstitutionCode()))
                    .findFirst()
                    .orElse(null);

            //GroupRepresentation tGroup= searchGroupsByName(keycloak,"ShootingStar",request.getInstitutionCode());

            List<GroupRepresentation> userGroups = keycloak.realm("ShootingStar")
                    .users()
                    .get(userId)
                    .groups();


            // Check if user is already in the group
            boolean alreadyInGroup = userGroups.stream()
                    .anyMatch(group -> group.getId().equals(tGroup.getId()));

            if (!alreadyInGroup) {
                keycloak.realm("ShootingStar").users().get(userId).joinGroup(tGroup.getId());
            }


            generatedPass=passwordGen();
            String mailBody = "<html><body>" +
                    "Please find herein, your credentials to the Orb School Application.<br>" +
                    "Username: " + staffCode + "<br>" +
                    "Password: " + generatedPass + "<br>" +
                    "Link to the app: <a href='https://orb.astromyllc.com'>https://orb.astromyllc.com</a>" +
                    "</body></html>";
                    //"Please find herein, your credentials to the Orb School Application.\n\nUsername: " + staffCode + "\n\nPassword: " + staffCode + "!23\n\nLink to the app: https://orb.astromyllc.com";

            // 2. Handle password for new users
            if (isNewUser.get()) {
                setInitialPassword(usersResource, userId, staffCode);

                // Send email asynchronously
               CompletableFuture.runAsync(() -> {
                    try {
                       String fromEmail=StaffUtil.institutionRequest.getName().replace(" ", ".");
                        int secondDotIndex = fromEmail.indexOf('.', fromEmail.indexOf('.') + 1);
                        fromEmail= fromEmail.substring(0, secondDotIndex );
                        mailUtil.sendTransactionalEmail(staffEmail, "User Credentials to the ORB application", mailBody,fromEmail);
                    } catch (Exception e) {
                        log.error("Failed to send email to {}", staffEmail, e);
                    }
                });
            }{
                String fromEmail=staffUtil.getInstitution(request.getInstitutionCode()) .getName().replace(" ", ".");
                int secondDotIndex = fromEmail.indexOf('.', fromEmail.indexOf('.') + 1);
                fromEmail= fromEmail.substring(0, secondDotIndex );
                mailUtil.sendTransactionalEmail(staffEmail, "User Credentials to the ORB application", mailBody,fromEmail);
            }

            // 3. Process role assignments
            processRoleAssignments(realmResource, usersResource, userId, staffPermissionsRequests);

            log.info("Successfully processed permissions for user: {}", staffCode);
        } catch (Exception e) {
            log.error("Failed to process Keycloak user credentials for {}", staffCode, e);
            throw new RuntimeException("User creation/update failed for " + staffCode, e);
        } finally {
            keycloak.close();
        }
    }

    private UserRepresentation findOrCreateUser(UsersResource usersResource, String staffCode, String staffEmail) {
        // Try to find existing user first
        List<UserRepresentation> users = usersResource.search(staffCode, true);
        if (!users.isEmpty()) {
            return users.get(0);
        }

        // Create new user if not found
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(staffCode);
        newUser.setEmail(staffEmail);
        newUser.setEnabled(true);



        Response response = usersResource.create(newUser);
        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus());
        }

        // Get the created user with retry logic
        return waitForUserCreation(usersResource, staffCode);
    }

    private UserRepresentation waitForUserCreation(UsersResource usersResource, String staffCode) {
        int attempts = 0;
        while (attempts < 3) {
            List<UserRepresentation> users = usersResource.search(staffCode);
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

    private void setInitialPassword(UsersResource usersResource, String userId, String staffCode) {
        CredentialRepresentation password = new CredentialRepresentation();
        password.setTemporary(true);
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(generatedPass);

        usersResource.get(userId).resetPassword(password);
        log.info("Password set successfully for user: {}", staffCode);
    }

    private void processRoleAssignments(RealmResource realmResource,
                                        UsersResource usersResource,
                                        String userId,
                                        List<StaffPermissionsRequest> requests) {
        // Separate roles to add and remove
        List<RoleRepresentation> rolesToAdd = requests.stream()
                .filter(req -> "add".equalsIgnoreCase(req.getState()))
                .map(req -> getRoleRepresentation(realmResource, req.getPermissionCode()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<RoleRepresentation> rolesToRemove = requests.stream()
                .filter(req -> "delete".equalsIgnoreCase(req.getState()))
                .map(req -> getRoleRepresentation(realmResource, req.getPermissionCode()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Process role assignments
        if (!rolesToAdd.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().add(rolesToAdd);
            log.info("Added roles {} to user {}", rolesToAdd, userId);
        }

        if (!rolesToRemove.isEmpty()) {
            usersResource.get(userId).roles().realmLevel().remove(rolesToRemove);
            log.info("Removed roles {} from user {}", rolesToRemove, userId);
        }
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

    private String passwordGen(){

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARS.length());
            password.append(CHARS.charAt(index));
        }

        return password.toString();
    }
}

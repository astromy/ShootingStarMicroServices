package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.model.StaffPermissions;
import com.astromyllc.shootingstar.hr.repository.StaffPermissionsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

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
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
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

    public static void KeyclaokCreateUserCredentials(List<StaffPermissionsRequest> staffPermissionsRequests) {

        // Authenticate and connect to Keycloak
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakURL)
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password("IdowhatIlikeIlikewhatIdo!@3")
                .clientId("admin-cli")
                .resteasyClient(
                        new ResteasyClientBuilder()
                                .connectionPoolSize(10).build()
                ).build();

        try {
            // Get the realm resource
            RealmResource realmResource = keycloak.realm("ShootingStar");

            /*String staffCode=staffPermissionsRequests.get(0).getStaffCode();
            List<UserRepresentation> users = realmResource.users().search(staffPermissionsRequests.get(0).getStaffCode());
            if (users.isEmpty()) {
                System.out.println("User not found!");
                return;
            }*/


            // Retrieve user by username
            UserRepresentation user = new UserRepresentation();
            UsersResource usersResource = realmResource.users();
            // Flag to check if the user is newly created
            AtomicBoolean isNewUser = new AtomicBoolean(false);

            String userId = usersResource.search(staffPermissionsRequests.get(0).getStaffCode()).stream()
                    .findFirst()
                    .map(euser -> euser.getId()) // If user exists, get their ID
                    .orElseGet(() -> { // If user doesn't exist, create the user
                        isNewUser.set(true); // Mark user as newly created
                        UserRepresentation newUser = new UserRepresentation();
                        newUser.setUsername(staffPermissionsRequests.get(0).getStaffCode());
                        newUser.setEnabled(true);

                        usersResource.create(newUser);

                        // Retrieve the created user's ID
                        return usersResource.search(staffPermissionsRequests.get(0).getStaffCode()).stream()
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User creation failed"))
                                .getId();
                    });

            // Step 3: Set User Password
            if (isNewUser.get()) {
            CredentialRepresentation password = new CredentialRepresentation();
            password.setTemporary(true); // temporary password
            password.setType(CredentialRepresentation.PASSWORD);
            password.setValue(staffPermissionsRequests.get(0).getStaffCode() + "123");

            usersResource.get(userId).resetPassword(password);
            log.info("Password set for user: " + staffPermissionsRequests.get(0).getStaffCode());
            } else {
                log.info("User already exists: " + staffPermissionsRequests.get(0).getStaffCode());
            }

            staffPermissionsRequests.stream()
                    .filter(request -> "add".equalsIgnoreCase(request.getState()))
                    .forEach(request -> System.out.println("Checking role: " + request.getPermissionCode()));

            // Separate roles to add and remove based on the "state" field
            List<RoleRepresentation> rolesToAdd = staffPermissionsRequests.stream()
                    .filter(request -> "add".equalsIgnoreCase(request.getState())) // Filter roles with state "add"
                    .map(roleName -> realmResource.roles().get(roleName.getPermissionCode()).toRepresentation())
                    .collect(Collectors.toList());

            List<RoleRepresentation> rolesToRemove = staffPermissionsRequests.stream()
                    .filter(request -> "delete".equalsIgnoreCase(request.getState())) // Filter roles with state "delete"
                    .map(roleName -> realmResource.roles().get(roleName.getPermissionCode()).toRepresentation())
                    .collect(Collectors.toList());

            // Step 5: Assign Roles to User (Add)
            if (!rolesToAdd.isEmpty()) {
                usersResource.get(userId).roles().realmLevel().add(rolesToAdd);

                for (RoleRepresentation rolesToAddItem:rolesToAdd) {
                    realmResource.users().get(userId).roles().realmLevel().add(Collections.singletonList(rolesToAddItem));
                }
                log.info("Roles added to user: " + rolesToAdd);
            }

            // Step 6: Remove Roles from User
            if (!rolesToRemove.isEmpty()) {
                usersResource.get(userId).roles().realmLevel().remove(rolesToRemove);

                for (RoleRepresentation rolesToRemoveItem:rolesToRemove) {
                    realmResource.users().get(userId).roles().realmLevel().remove(Collections.singletonList(rolesToRemoveItem));
                }
                log.info("Roles removed from user: " + rolesToRemove);
            }

            log.info("Roles assigned to user: " + staffPermissionsRequests.get(0).getStaffCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            keycloak.close(); // Close the Keycloak client
        }
    }

}

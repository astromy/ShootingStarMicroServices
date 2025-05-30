package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.PreOrderInstitution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.PreOrderInstitutionRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstitutionUtils {

    public static List<Institution> institutionGlobalList = null;
    public static List<PreOrderInstitution> preOrderInstitutionGlobalList = null;
    public static List<String> permissions = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final InstitutionRepository institutionRepository;
    private final PreOrderInstitutionRepository preOrderInstitutionRepository;
    private final SubjectUtil subjectUtil;
    private final MailUtil mailUtil;
    @Value("${gateway.host}")
    private String keycloakSecrete;
    @Value("${keycloak.address}")
    private String keycloakURL;

    @PostConstruct
    public void fetAllInstitutions() {
        institutionGlobalList = institutionRepository.findAll();
        preOrderInstitutionGlobalList = preOrderInstitutionRepository.findAll();
        log.info("Global Institution List populated with {} records", (long) institutionGlobalList.size());
        log.info("Global Pre-Ordered Institution List populated with {} records", (long) preOrderInstitutionGlobalList.size());
    }

    public Institution mapInstitutionRequest_ToInstitution(InstitutionRequest institutionRequest) {
        return Institution.builder()
                .name(institutionRequest.getName())
                .slogan(institutionRequest.getSlogan())
                .country(institutionRequest.getCountry())
                .region(institutionRequest.getRegion())
                .city(institutionRequest.getCity())
                .email(institutionRequest.getEmail())
                .website(institutionRequest.getWebsite())
                .contact1(institutionRequest.getContact1())
                .contact2(institutionRequest.getContact2())
                .status(institutionRequest.getStatus())
                .bececode(institutionRequest.getBececode())
                .creationDate(LocalDate.now())
                .postalAddress(institutionRequest.getPostalAddress())
                .streams(institutionRequest.getStreams())
                .subscription(institutionRequest.getSubscription())
                .crest(institutionRequest.getCrest())
                .headSignature(institutionRequest.getHeadSignature())
                .admissions(AdmissionUtil.mapAdmissionRequestToAdmission(institutionRequest.getAdmissions()))
                .classList(institutionRequest.getClassList().stream().map(ClassesUtil::mapClassRequestToClass).toList())
                .gradingSetting(institutionRequest.getGradingSetting().stream().map(GradingSettingUtil::mapGradeSettingRequest_ToGradeSetting).toList())
                .subjectList(institutionRequest.getSubjectList().stream().map(SubjectUtil::mapSubjectRequest_ToSubject).toList())
                .departmentList(institutionRequest.getDepartmentList().stream().map(DepartmentUtil::mapDepartmentRequest_ToDepartment).toList())
                .build();
    }

    public InstitutionResponse mapInstitutionToInstitutionResponse(Institution institution) {
        return InstitutionResponse.builder()
                .id(institution.getIdInstitution())
                .name(institution.getName())
                .slogan(institution.getSlogan())
                .country(institution.getCountry())
                .region(institution.getRegion())
                .city(institution.getCity())
                .email(institution.getEmail())
                .website(institution.getWebsite())
                .contact1(institution.getContact1())
                .contact2(institution.getContact2())
                .status(institution.getStatus())
                .bececode(institution.getBececode())
                .creationDate(institution.getCreationDate())
                .postalAddress(institution.getPostalAddress())
                .streams(institution.getStreams())
                .subscription(institution.getSubscription())
                .crest(institution.getCrest())
                .headSignature(institution.getHeadSignature())
                .admissions(institution.getAdmissions() != null
                        ? AdmissionUtil.mapAdmissionToAdmissionResponse(institution.getAdmissions())
                        : null)
                .classList(institution.getClassList() != null
                        ? institution.getClassList().stream()
                        .filter(Objects::nonNull)
                        .map(ClassesUtil::mapClassToClassResponse)
                        .toList()
                        : Collections.emptyList())
                .gradingSetting(Optional.ofNullable(institution.getGradingSetting())
                        .map(list -> list.stream()
                                .filter(Objects::nonNull)
                                .map(GradingSettingUtil::mapGradeSetting_ToGradeSettingResponse)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList())
                        .filter(list -> !list.isEmpty()))

                .subjectList(institution.getSubjectList() != null
                        ? institution.getSubjectList().stream()
                        .filter(Objects::nonNull)
                        .map(subjectUtil::mapSubject_ToSubjectResponse)
                        .toList()
                        : Collections.emptyList())
                .classList(institution.getClassList() != null
                        ? institution.getClassList().stream()
                        .filter(Objects::nonNull)
                        .map(ClassesUtil::mapClassToClassResponse)
                        .toList()
                        : Collections.emptyList())
                .departmentList(institution.getDepartmentList() != null
                        ? institution.getDepartmentList().stream()
                        .filter(Objects::nonNull)
                        .map(DepartmentUtil::mapDepartment_ToDepartmentResponse)
                        .toList()
                        : Collections.emptyList())
                .build();
    }

    public Institution mapInstitutionRequestToInstitution(Institution institution, InstitutionRequest institutionRequest) {
        institution.setName(institutionRequest.getName());
        institution.setSlogan(institutionRequest.getSlogan());
        institution.setCity(institutionRequest.getCity());
        institution.setRegion(institutionRequest.getRegion());
        institution.setCountry(institutionRequest.getCountry());
        institution.setBececode(institutionRequest.getBececode());
        institution.setContact1(institutionRequest.getContact1());
        institution.setContact2(institutionRequest.getContact2());
        institution.setEmail(institutionRequest.getEmail());
        institution.setPostalAddress(institutionRequest.getPostalAddress());
        institution.setStatus(institutionRequest.getStatus());
        institution.setStreams(institutionRequest.getStreams());
        institution.setWebsite(institutionRequest.getWebsite());
        institution.setSubscription(institutionRequest.getSubscription());
        institution.setCrest(institutionRequest.getCrest());
        institution.setHeadSignature(institutionRequest.getHeadSignature());
        //institution.setCreationDate(LocalDate.parse(institutionRequest.getCreationDate().replace("T", " "), formatter));
        //institution.setAdmissions(AdmissionUtil.mapAdmissionRequestToAdmission(institutionRequest.getAdmissions(), institution.getAdmissions()));
        //institution.setClassList(institutionRequest.getClassList().stream().map((cr) -> ClassesUtil.mapClassRequestToClass(cr, institution.getClassList().stream().filter(c -> cr.getId().equalsIgnoreCase(c.getIdClasses())).findFirst().get())).toList());
        //institution.setDepartmentList(institutionRequest.getDepartmentList().stream().map((dr) -> DepartmentUtil.mapDepartmentRequest_ToDepartment(dr, institution.getDepartmentList().stream().filter(d -> dr.getIdDepartment().equalsIgnoreCase(d.getIdDepartment())).findFirst().get())).toList());
        //institution.setGradingSetting(GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(institutionRequest.getGradingSetting(), institution.getGradingSetting()));
        //institution.setSubjectList(institutionRequest.getSubjectList().stream()
        // .map((sr) -> SubjectUtil.mapSubjectRequest_ToSubject(sr, institution.getSubjectList().stream().filter(s -> sr.getId().equalsIgnoreCase(s.getIdSubject())).findFirst().get())).toList());
        return institution;
    }

    public Institution mapPreorderInstitutionToInstitution(PreOrderInstitution preOrderInstitution) {
        return Institution.builder()
                .name(preOrderInstitution.getName())
                .slogan(preOrderInstitution.getSlogan())
                .city(preOrderInstitution.getCity())
                .region(preOrderInstitution.getRegion())
                .country(preOrderInstitution.getCountry())
                .bececode(preOrderInstitution.getBececode())
                .contact1(preOrderInstitution.getContact1())
                .contact2(preOrderInstitution.getContact2())
                .email(preOrderInstitution.getEmail())
                .postalAddress(preOrderInstitution.getPostalAddress())
                .status(preOrderInstitution.getStatus())
                .streams(preOrderInstitution.getStreams())
                .website(preOrderInstitution.getWebsite())
                .subscription(preOrderInstitution.getSubscription())
                .creationDate(preOrderInstitution.getCreationDate())
                .crest(preOrderInstitution.getCrest())
                .build();
    }

    public PreOrderInstitution mapPreOrderInstitutionRequest_ToPreOrderInstitution(PreOrderInstitutionRequest institutionRequest) {
        return PreOrderInstitution.builder()
                .name(institutionRequest.getName())
                .slogan(institutionRequest.getSlogan())
                .country(institutionRequest.getCountry())
                .region(institutionRequest.getRegion())
                .city(institutionRequest.getCity())
                .email(institutionRequest.getEmail())
                .website(institutionRequest.getWebsite())
                .contact1(institutionRequest.getContact1())
                .contact2(institutionRequest.getContact2())
                .status(institutionRequest.getStatus())
                .bececode(institutionRequest.getBececode())
                .creationDate(LocalDate.now())
                .postalAddress(institutionRequest.getPostalAddress())
                .streams(institutionRequest.getStreams())
                .subscription(institutionRequest.getSubscription())
                .population(institutionRequest.getPopulation())
                .crest(institutionRequest.getCrest())
                .build();
    }

    public PreOrderInstitutionResponse mapPreOrderInstitutionToPreOrderInstitutionResponse(PreOrderInstitution institution) {
        return PreOrderInstitutionResponse.builder()
                .id(institution.getIdInstitution())
                .name(institution.getName())
                .slogan(institution.getSlogan())
                .country(institution.getCountry())
                .region(institution.getRegion())
                .city(institution.getCity())
                .email(institution.getEmail())
                .website(institution.getWebsite())
                .contact1(institution.getContact1())
                .contact2(institution.getContact2())
                .status(institution.getStatus())
                .bececode(institution.getBececode())
                .creationDate(institution.getCreationDate())
                .postalAddress(institution.getPostalAddress())
                .streams(institution.getStreams())
                .subscription(institution.getSubscription())
                .population(institution.getPopulation())
                .crest(institution.getCrest())
                .build();
    }


    public void createKeycloakCredentials(PreOrderInstitution institution) {
        List<String> permissions = new ArrayList<String>();
        permissions.add("setup classes");
        permissions.add("setup subject");
        permissions.add("setup admission");
        permissions.add("setup department");
        permissions.add("setup grading");
        permissions.add("setup permissions");
        permissions.add("setup institution");
        permissions.add("setup classgroup");

        permissions.add("Human_Resource onboarding");
        permissions.add("Human_Resource records");
        permissions.add("Human_Resource leave");
        permissions.add("Human_Resource appraisals");
        permissions.add("Human_Resource designation");
        permissions.add("Human_Resource offboarding");

        permissions.add("Finance bill_creation");
        permissions.add("Finance billing");
        permissions.add("Finance fee_collection");
        permissions.add("Finance payment_history");
        permissions.add("Finance payment_checker");
        permissions.add("Finance salary_setup");
        permissions.add("Finance payslip");
        permissions.add("Finance ledger_books");
        permissions.add("Finance income_statement");
        permissions.add("Finance cash_flow");
        permissions.add("Finance trial_balance");

        permissions.add("Academics question_upload_approval");
        permissions.add("Academics score_upload");
        permissions.add("Academics transcript");
        permissions.add("Academics broadsheet");
        permissions.add("Academics graduation_list");
        permissions.add("Academics class_timetable");
        permissions.add("Academics promotions_and_demotions");
        permissions.add("Academics terminal_report");

        permissions.add("Administration student_enrollment");
        permissions.add("Administration student_record");
        permissions.add("Administration suspended");
        permissions.add("Administration dismissed");
        permissions.add("Administration student_list");
        permissions.add("Administration class_list");
        permissions.add("Administration academic_timetable");
        permissions.add("Administration id_card_generation");

        permissions.add("Infirmary vitals_recording");
        permissions.add("Infirmary diagnosis_recording");
        permissions.add("Infirmary medical_history");

        permissions.add("Stores sales");
        permissions.add("Stores inventory");
        permissions.add("Stores insight");

        permissions.add("Teaching question_upload");
        permissions.add("Teaching exams");
        permissions.add("Teaching assignment");
        permissions.add("Teaching score_upload");
        permissions.add("Teaching assignment_review");

        String clientSecret = keycloakSecrete;

        Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(keycloakURL)
                .realm("ShootingStar")
                .clientId("astro_orb_microservices")
                .clientSecret("VSOLTuWTCIasuAK5xNS93YOKfmnwtUlE")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        /**
         * Create Groups(Institution) and subGroups (User types)
         */
        RealmResource realm = kc.realm("ShootingStar");
        GroupRepresentation topGroup = new GroupRepresentation();
        topGroup.setName(institution.getBececode());
        createGroup(realm, topGroup);

        createSubGroup(realm, topGroup.getId(), "Admin");
        createSubGroup(realm, topGroup.getId(), "User");

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(institution.getBececode());

        /**
         * Create Default School admin User
         */
        List<CredentialRepresentation> credentials = new ArrayList<CredentialRepresentation>();
        List<String> sysRoles = new ArrayList<String>();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(institution.getBececode());
        credentials.add(credential);

        sysRoles.add(institution.getBececode() + "/Admin");
        sysRoles.add(institution.getBececode() + "/User");
        sysRoles.add(institution.getBececode());

        String client = "Admin" + institution.getBececode();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(client);
        userRepresentation.setEmail(institution.getEmail());
        userRepresentation.setCredentials(credentials);
        userRepresentation.setRealmRoles(permissions);
        userRepresentation.setEnabled(true);
        userRepresentation.setGroups(sysRoles);

        try {
            kc.realm("ShootingStar").users().create(userRepresentation);

            // Get the realm resource
            RealmResource realmResource = kc.realm("ShootingStar");


            // Retrieve user by username
            UserRepresentation user = new UserRepresentation();
            UsersResource usersResource = realmResource.users();
            AtomicBoolean isNewUser = new AtomicBoolean(false);

            String userId = usersResource.search(client).stream()
                    .findFirst()
                    .map(UserRepresentation::getId) // If user exists, get their ID
                    .orElseGet(() -> { // If user doesn't exist, create the user
                        isNewUser.set(true); // Mark user as newly created
                        UserRepresentation newUser = new UserRepresentation();
                        newUser.setUsername(client);
                        newUser.setEnabled(true);

                        usersResource.create(newUser);

                        // Retrieve the created user's ID
                        return usersResource.search(client).stream()
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("User creation failed"))
                                .getId();
                    });

            // Separate roles to add and remove based on the "state" field
            List<RoleRepresentation> rolesToAdd = permissions.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .toList();

            // Step 5: Assign Roles to User (Add)
            if (!rolesToAdd.isEmpty()) {
                usersResource.get(userId).roles().realmLevel().add(rolesToAdd);
                log.info("Roles added to user: " + rolesToAdd);
            }

            sendmail(institution,client,institution.getBececode(),institution.getEmail());
            alertMail(institution,"astromyllc@gmail.com");


        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private void createSubGroup(RealmResource realm, String parentGroupId, String subGroupName) {
        GroupRepresentation subgroup = new GroupRepresentation();
        subgroup.setName(subGroupName);
        try (Response response = realm.groups().group(parentGroupId).subGroup(subgroup)) {
            if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
                System.out.println("Created Subgroup : " + subGroupName);
            } else {
                //logger.severe("Error Creating Subgroup : " + subGroupName + ", Error Message : " + getErrorMessage(response));
            }
        }
    }

    private GroupRepresentation createGroup(RealmResource realm, GroupRepresentation group) {
        try (Response response = realm.groups().add(group)) {
            String groupId = getCreatedId(response);
            group.setId(groupId);
            return group;
        }
    }

    private void sendmail(PreOrderInstitution inst, String userName, String passCode, String reciepient) {
        String mailBody = "<html><body>" +

                "Hi "+ inst.getName() +" Team,.<br>" +

                "Welcome aboard! We’re thrilled to have you as part of our growing community. Your subscription is now active, and we’re excited to support your journey every step of the way..<br>" +

                "Please find herein, your credentials to the Orb School Application.<br>" +
                "Username: " + userName + "<br>" +
                "Password: " + passCode + "!23<br>" +
                "Link to the app: <a href='https://orb.astromyllc.com'>https://orb.astromyllc.com</a>" +

                "If you have any questions or need help getting started, we're just a message away..<br>" +

                "Here’s to a great partnership! 🚀.<br>" +

                "Warm regards,.<br>" +
                "support@astromyllc.com.<br>" +
                "Astromy ORB Services.<br>" +

                "</body></html>";


        mailUtil.sendTransactionalEmail(reciepient, "Welcome to the Astromy ORB application", mailBody,"support");
    }

    private void alertMail(PreOrderInstitution inst, String reciepient) {
        String mailBody = "<html><body>" +

                "A new Client has filled out the pre-subscription for the ORB application<br>" +

                "Please find herein, the details of the institution.<br>" +
                "Institution Name             : " + inst.getName() + "<br>" +
                "Institution Population       : " + inst.getPopulation() + "<br>" +
                "Institution Contact          : " + inst.getContact1() + "<br>" +
                "Institution email            : " + inst.getEmail() + "<br>" +
                "Institution Name             : " + inst.getWebsite() + "<br>" +
                "Institution City             : " + inst.getCity() + "<br>" +
                "Institution Subscription Type: " + inst.getSubscription() + "<br>" +

                "Warm regards,.<br>" +
                "support@astromyllc.com.<br>" +
                "Astromy ORB Services.<br>" +

                "</body></html>";


        mailUtil.sendTransactionalEmail(reciepient, "NEW ORB SUBSCRIPTION", mailBody,"support");
    }

    @Bean
    private void createPermissions() {
        List<String> permissions = new ArrayList<String>();
        permissions.add("setup classes");
        permissions.add("setup subject");
        permissions.add("setup admission");
        permissions.add("setup department");
        permissions.add("setup grading");
        permissions.add("setup permissions");
        permissions.add("setup institution");
        permissions.add("setup classgroup");
        permissions.add("setup accommodation");

        permissions.add("Human_Resource onboarding");
        permissions.add("Human_Resource records");
        permissions.add("Human_Resource leave");
        permissions.add("Human_Resource appraisals");
        permissions.add("Human_Resource designation");
        permissions.add("Human_Resource offboarding");

        permissions.add("Finance bill_creation");
        permissions.add("Finance billing");
        permissions.add("Finance fee_collection");
        permissions.add("Finance payment_history");
        permissions.add("Finance payment_checker");
        permissions.add("Finance salary_setup");
        permissions.add("Finance payslip");
        permissions.add("Finance ledger_books");
        permissions.add("Finance income_statement");
        permissions.add("Finance cash_flow");
        permissions.add("Finance trial_balance");

        permissions.add("Academics question_upload_approval");
        permissions.add("Academics score_upload");
        permissions.add("Academics transcript");
        permissions.add("Academics broadsheet");
        permissions.add("Academics graduation_list");
        permissions.add("Academics class_timetable");
        permissions.add("Academics promotions_and_demotions");
        permissions.add("Academics terminal_report");

        permissions.add("Administration student_enrollment");
        permissions.add("Administration student_record");
        permissions.add("Administration suspended");
        permissions.add("Administration dismissed");
        permissions.add("Administration student_list");
        permissions.add("Administration class_list");
        permissions.add("Administration academic_timetable");
        permissions.add("Administration id_card_generation");

        permissions.add("Infirmary vitals_recording");
        permissions.add("Infirmary diagnosis_recording");
        permissions.add("Infirmary medical_history");

        permissions.add("Accommodation accommodationStudentList");
        permissions.add("Accommodation roomAllocation");
        permissions.add("Accommodation dutyRoster");
        permissions.add("Accommodation resourceTracking");
        permissions.add("Accommodation positions");
        permissions.add("Accommodation faults");

        permissions.add("Stores sales");
        permissions.add("Stores inventory");
        permissions.add("Stores insight");

        permissions.add("Teaching question_upload");
        permissions.add("Teaching exams");
        permissions.add("Teaching assignment");
        permissions.add("Teaching score_upload");
        permissions.add("Teaching assignment_review");


        Keycloak kc = KeycloakBuilder.builder()
                .serverUrl(keycloakURL)
                .realm("ShootingStar")
                .clientId("astro_orb_microservices")
                .clientSecret("VSOLTuWTCIasuAK5xNS93YOKfmnwtUlE")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        for (String role : permissions) {
            try {
                // Check if the role already exists
                Optional<RoleRepresentation> existingRole = kc.realm("ShootingStar").roles().list().stream()
                        .filter(r -> r.getName().equals(role))
                        .findFirst();

                if (existingRole.isPresent()) {
                    System.out.println("Role already exists: " + role);
                } else {
                    // Create the role if it does not exist
                    RoleRepresentation roleRepresentation = new RoleRepresentation();
                    roleRepresentation.setName(role);
                    kc.realm("ShootingStar").roles().create(roleRepresentation);
                    System.out.println("Role created: " + role);
                }
            } catch (Exception e) {
                System.out.println("Error processing role " + role + ": " + e.getMessage());
            }
        }
    }

}

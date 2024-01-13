package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.PreOrderInstitution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.PreOrderInstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.keycloak.admin.client.CreatedResponseUtil.getCreatedId;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstitutionUtils {
    private final InstitutionRepository institutionRepository;
    private final PreOrderInstitutionRepository preOrderInstitutionRepository;
    public static List<Institution> institutionGlobalList = null;
    public static List<PreOrderInstitution> preOrderInstitutionGlobalList = null;
    public static List<String> permissions = null;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    public void fetAllInstitutions() {
        institutionGlobalList = institutionRepository.findAll();
        preOrderInstitutionGlobalList = preOrderInstitutionRepository.findAll();
        log.info("Global Institution List populated with {} records", institutionGlobalList.stream().count());
        log.info("Global Pre-Ordered Institution List populated with {} records", preOrderInstitutionGlobalList.stream().count());
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
                .admissions(AdmissionUtil.mapAdmissionRequestToAdmission(institutionRequest.getAdmissions()))
                .classList(institutionRequest.getClassList().stream().map(c -> ClassesUtil.mapClassRequestToClass(c)).toList())
                .gradingSetting(GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(institutionRequest.getGradingSetting()))
                .subjectList(institutionRequest.getSubjectList().stream().map(s -> SubjectUtil.mapSubjectRequest_ToSubject(s)).toList())
                .departmentList(institutionRequest.getDepartmentList().stream().map(d -> DepartmentUtil.mapDepartmentRequest_ToDepartment(d)).toList())
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
                .admissions(AdmissionUtil.mapAdmissionToAdmissionResponse(institution.getAdmissions()))
                .classList(institution.getClassList().stream().map(ClassesUtil::mapClassToClassResponse).toList())
                .gradingSetting(GradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(institution.getGradingSetting()))
                .subjectList(institution.getSubjectList().stream().map(SubjectUtil::mapSubject_ToSubjectResponse).toList())
                .classList(institution.getClassList().stream().map(ClassesUtil::mapClassToClassResponse).toList())
                .departmentList(institution.getDepartmentList().stream().map(DepartmentUtil::mapDepartment_ToDepartmentResponse).toList())
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
        //institution.setCreationDate(LocalDate.parse(institutionRequest.getCreationDate().replace("T", " "), formatter));
        //institution.setAdmissions(AdmissionUtil.mapAdmissionRequestToAdmission(institutionRequest.getAdmissions(), institution.getAdmissions()));
        //institution.setClassList(institutionRequest.getClassList().stream().map((cr) -> ClassesUtil.mapClassRequestToClass(cr, institution.getClassList().stream().filter(c -> cr.getId().equals(c.getIdClasses())).findFirst().get())).collect(Collectors.toList()));
        //institution.setDepartmentList(institutionRequest.getDepartmentList().stream().map((dr) -> DepartmentUtil.mapDepartmentRequest_ToDepartment(dr, institution.getDepartmentList().stream().filter(d -> dr.getIdDepartment().equals(d.getIdDepartment())).findFirst().get())).collect(Collectors.toList()));
        //institution.setGradingSetting(GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(institutionRequest.getGradingSetting(), institution.getGradingSetting()));
        //institution.setSubjectList(institutionRequest.getSubjectList().stream()
               // .map((sr) -> SubjectUtil.mapSubjectRequest_ToSubject(sr, institution.getSubjectList().stream().filter(s -> sr.getId().equals(s.getIdSubject())).findFirst().get())).collect(Collectors.toList()));
        return institution;
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

        String clientSecret = "ywUvVeSUVbbwgsfABUNWyKPJimFXkcwJ";
        /*Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://keycloak:8080/auth")
                .realm("ShootingStar")
                .clientId("admin-cli")
                .username("admin")
                .password("IdowhatIlikeIlikewhatIdo!@3")
                .build();
                */

        Keycloak kc = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8090/")
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password("IdowhatIlikeIlikewhatIdo!@3")
                .clientId("admin-cli")
                .resteasyClient(
                        new ResteasyClientBuilder()
                                .connectionPoolSize(10).build()
                ).build();

        /**
         * Create Groups(Institution) and subGroups (User types)
         */
        RealmResource realm = kc.realm("ShootingStar");
        GroupRepresentation topGroup = new GroupRepresentation();
        topGroup.setName(institution.getBececode());
        topGroup = createGroup(realm, topGroup);

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

        sysRoles.add(institution.getBececode()+"/Admin");
        sysRoles.add(institution.getBececode()+"/User");
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


    @Bean
    private void createPermissions() {
        List<String> permissions = new ArrayList<String>();
        permissions.add("setup classes");
        permissions.add("setup subject");
        permissions.add("setup admission");
        permissions.add("setup department");
        permissions.add("setup grading");
        permissions.add("setup permissions");
        permissions.add("Human_Resouce onloading");
        permissions.add("Human_Resouce records");
        permissions.add("Human_Resouce leave");
        permissions.add("Human_Resouce appraisals");
        permissions.add("Human_Resouce designation");
        permissions.add("Human_Resouce offloading");
        permissions.add("Finance billcreation");
        permissions.add("Finance billing");
        permissions.add("Finance feecollection");
        permissions.add("Finance paymenthistory");
        permissions.add("Finance paymentchecker");
        permissions.add("Finance salarysetup");
        permissions.add("Finance payslipgeneration");
        permissions.add("Finance ledgerbooks");
        permissions.add("Finance incomestatement");
        permissions.add("Finance cashflow");
        permissions.add("Finance trialbalanace");


        Keycloak kc = KeycloakBuilder.builder()
                .serverUrl("http://localhost:8090/")
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password("IdowhatIlikeIlikewhatIdo!@3")
                .clientId("admin-cli")
                .resteasyClient(
                        new ResteasyClientBuilder()
                                .connectionPoolSize(10).build()
                ).build();

        for (String role : permissions) {
            try {
                RoleRepresentation roleRepresentation = new RoleRepresentation();
                roleRepresentation.setName(role);
                kc.realm("ShootingStar").roles().create(roleRepresentation);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}

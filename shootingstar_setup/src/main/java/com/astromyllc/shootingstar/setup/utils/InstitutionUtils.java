package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstitutionUtils {
    private final InstitutionRepository institutionRepository;
    public static List<Institution> institutionGlobalList = null;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    private void fetAllInstitutions() {
        institutionGlobalList = institutionRepository.findAll();
        log.info("Global Institution List populated with {} records", institutionGlobalList.stream().count());
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
                .admissions(AdmissionUtil.mapAdmissionRequestToAdmission(institution.getAdmissions()))
                .classList(institution.getClassList().stream().map(c -> ClassesUtil.mapClassToClassResponse(c)).toList())
                .gradingSetting(GradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(institution.getGradingSetting()))
                .subjectList(institution.getSubjectList().stream().map(s -> SubjectUtil.mapSubject_ToSubjectResponse(s)).toList())
                .departmentList(institution.getDepartmentList().stream().map(d -> DepartmentUtil.mapDepartment_ToDepartmentResponse(d)).toList())
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
        institution.setCreationDate(LocalDate.parse(institutionRequest.getCreationDate().replace("T"," "), formatter));
        institution.setAdmissions(AdmissionUtil.mapAdmissionRequestToAdmission(institutionRequest.getAdmissions(),institution.getAdmissions()));
        institution.setClassList(institutionRequest.getClassList().stream().map((cr) -> ClassesUtil.mapClassRequestToClass(cr, institution.getClassList().stream().filter(c->cr.getId().equals(c.getIdClasses())).findFirst().get())).collect(Collectors.toList()));
        institution.setDepartmentList(institutionRequest.getDepartmentList().stream().map((dr) -> DepartmentUtil.mapDepartmentRequest_ToDepartment(dr,institution.getDepartmentList().stream().filter(d->dr.getIdDepartment().equals(d.getIdDepartment())).findFirst().get())).collect(Collectors.toList()));
        institution.setGradingSetting(GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(institutionRequest.getGradingSetting(),institution.getGradingSetting()));
        institution.setSubjectList(institutionRequest.getSubjectList().stream().map((sr) -> SubjectUtil.mapSubjectRequest_ToSubject(sr,institution.getSubjectList().stream().filter(s->sr.getId().equals(s.getIdSubject())).findFirst().get())).collect(Collectors.toList()));
        return institution;
    }
}

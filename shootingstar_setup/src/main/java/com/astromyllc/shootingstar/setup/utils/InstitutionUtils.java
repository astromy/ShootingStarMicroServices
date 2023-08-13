package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.Subject;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.service.AdmissionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstitutionUtils {
    private final InstitutionRepository institutionRepository;
    public static List<Institution> institutionGlobalList=null;
        @Bean
    private void fetAllInstitutions(){
        institutionGlobalList=institutionRepository.findAll();
        log.info("Global Institution List populated with {} records",institutionGlobalList.stream().count());
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
                .classList((List<Classes>) institutionRequest.getClassList().stream().map(c->{
                    return ClassesUtil.mapClassRequestToClass(c);
                }).toList())
                .gradingSetting(GradingSettingUtil.mapGradeSettingRequest_ToGradeSetting(institutionRequest.getGradingSetting()))
                .subjectList((List<Subject>) institutionRequest.getSubjectList().stream().map(s->{
                    return SubjectUtil.mapSubjectRequest_ToSubject(s);
                }).toList())
                .build();
    }

    public InstitutionResponse mapInstitutionToInstitutionResponse(Institution institution) {
        return    InstitutionResponse.builder()
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
                .classList((List<ClassesResponse>) institution.getClassList().stream().map(c->{
                    return ClassesUtil.mapClassToClassResponse(c);
                }).toList())
                .gradingSetting(GradingSettingUtil.mapGradeSetting_ToGradeSettingResponse(institution.getGradingSetting()))
                .subjectList((List<SubjectResponse>) institution.getSubjectList().stream().map(s->{
                    return SubjectUtil.mapSubject_ToSubjectResponse(s);
                }).toList())
                .build();
    }

    public Institution mapInstitutionRequestToInstitution(Institution institution, InstitutionRequest institutionRequest) {

        return institution;
    }
}

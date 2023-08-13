package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.AdmissionCriteriaRequest;
import com.astromyllc.shootingstar.setup.dto.request.AdmissionsRequest;
import com.astromyllc.shootingstar.setup.dto.request.ApplicationCategoryRequest;
import com.astromyllc.shootingstar.setup.dto.response.AdmissionCriteriaResponse;
import com.astromyllc.shootingstar.setup.dto.response.AdmissionsResponse;
import com.astromyllc.shootingstar.setup.dto.response.ApplicationCategoryResponse;
import com.astromyllc.shootingstar.setup.model.AdmissionCriteria;
import com.astromyllc.shootingstar.setup.model.Admissions;
import com.astromyllc.shootingstar.setup.model.ApplicationCategory;
import com.astromyllc.shootingstar.setup.repository.AdmissionsRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdmissionUtil {
    private final AdmissionsRepository admissionsRepository;
    public static List<Admissions> admissionsGlobalList =null;


    @Bean
    private void getAllAdmissions(){
        admissionsGlobalList =admissionsRepository.findAll();
        log.info("Global Admission setting List populated with {} records", admissionsGlobalList.stream().count());
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Admissions mapAdmissionRequestToAdmission(AdmissionsRequest admissionsRequest) {
      return  Admissions.builder()
                .admissionCriteriaList((List<AdmissionCriteria>) admissionsRequest.getAdmissionCriteriaList().stream().map(criteria->{
                    return mapCriteriaRequestToCriteria(criteria);
                }).toList())
                .applicationCategoryList((List<ApplicationCategory>)admissionsRequest.getApplicationCategoryList().stream().map(category->{
                    return mapCategoryRequestToCategory(category);
                }).toList())
                .build();
    }

    public static AdmissionsResponse mapAdmissionRequestToAdmission(Admissions admissions) {
        return  AdmissionsResponse.builder()
                .id(admissions.getIdAdmissions())
                .admissionCriteriaList((List<AdmissionCriteriaResponse>) admissions.getAdmissionCriteriaList().stream().map(criteria->{
                    return mapCriteriaToCriteriaResponse(criteria);
                }).toList())
                .applicationCategoryList((List<ApplicationCategoryResponse>)admissions.getApplicationCategoryList().stream().map(category->{
                    return mapCategoryToCategoryResponse(category);
                }).toList())
                .build();
    }


    private static AdmissionCriteriaResponse mapCriteriaToCriteriaResponse(AdmissionCriteria criteria) {
        return AdmissionCriteriaResponse.builder()
                .id(criteria.getIdAdmissionCriteria())
                .criteria(criteria.getCriteria())
                .value(criteria.getValue())
                .build();
    }

    private static AdmissionCriteria mapCriteriaRequestToCriteria(AdmissionCriteriaRequest admissionCriteriaRequest) {
        return AdmissionCriteria.builder()
                .criteria(admissionCriteriaRequest.getCriteria())
                .value(admissionCriteriaRequest.getValue())
                .build();
    }
    private static ApplicationCategory mapCategoryRequestToCategory(ApplicationCategoryRequest applicationCategoryRequest) {
        return ApplicationCategory.builder()
                .applicationFormType(applicationCategoryRequest.getApplicationFormType())
                .applicationFormAmount(applicationCategoryRequest.getApplicationFormAmount())
                .applicationFormQNT(applicationCategoryRequest.getApplicationFormQNT())
                .appointmentClosure(LocalDateTime.parse(applicationCategoryRequest.getAppointmentClosure(),formatter))
                .appointmentCommencement(LocalDateTime.parse(applicationCategoryRequest.getAppointmentCommencement(),formatter))
                .paymentMedium(applicationCategoryRequest.getPaymentMedium())
                .closure(LocalDateTime.parse(applicationCategoryRequest.getClosure(),formatter))
                .commencement(LocalDateTime.parse(applicationCategoryRequest.getCommencement(),formatter))
                .appointmentPerDay(applicationCategoryRequest.getAppointmentPerDay())

                .build();
    }

    private static ApplicationCategoryResponse mapCategoryToCategoryResponse(ApplicationCategory category) {
        return ApplicationCategoryResponse.builder()
                .id(category.getIdApplicationCategory())
                .applicationFormType(category.getApplicationFormType())
                .applicationFormAmount(category.getApplicationFormAmount())
                .applicationFormQNT(category.getApplicationFormQNT())
                .appointmentClosure(category.getAppointmentClosure())
                .appointmentCommencement(category.getAppointmentCommencement())
                .paymentMedium(category.getPaymentMedium())
                .closure(category.getClosure())
                .commencement(category.getCommencement())
                .appointmentPerDay(category.getAppointmentPerDay())

                .build();
    }

}

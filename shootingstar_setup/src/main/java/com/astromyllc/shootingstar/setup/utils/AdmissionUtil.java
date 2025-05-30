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
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdmissionUtil {
    private final AdmissionsRepository admissionsRepository;
    public static List<Admissions> admissionsGlobalList =null;


    @PostConstruct
    private void getAllAdmissions(){
        admissionsGlobalList =admissionsRepository.findAll();
        log.info("Global Admission setting List populated with {} records", admissionsGlobalList.stream().count());
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Admissions mapAdmissionRequestToAdmission(AdmissionsRequest admissionsRequest) {
      return  Admissions.builder()
                .admissionCriteriaList(admissionsRequest.getAdmissionCriteriaList().stream().map(criteria->{
                    return mapCriteriaRequestToCriteria(criteria);
                }).toList())
                .applicationCategoryList(admissionsRequest.getApplicationCategoryList().stream().map(category->{
                    return mapCategoryRequestToCategory(category);
                }).toList())
                .build();
    }

    public static Admissions mapAdmissionRequestToAdmission(AdmissionsRequest admissionsRequest,Admissions a) {
                a.setAdmissionCriteriaList(admissionsRequest.getAdmissionCriteriaList().stream().map(criteria->{
                    return mapCriteriaRequestToCriteria(criteria,a.getAdmissionCriteriaList().stream().filter(c->criteria.getId().equals(c.getIdAdmissionCriteria())).findFirst().get());
                }).toList());
                a.setApplicationCategoryList(admissionsRequest.getApplicationCategoryList().stream().map(category->{
                    return mapCategoryRequestToCategory(category,a.getApplicationCategoryList().stream().filter(c->category.getId().equals(c.getIdApplicationCategory())).findFirst().get());
                }).toList());
                return  a;
    }

    public static Optional<AdmissionsResponse> mapAdmissionToAdmissionResponse(Admissions admissions) {
        return Optional.ofNullable(admissions)
                .map(admission->AdmissionsResponse.builder()
                    .id(admission.getIdAdmissions())
                    .admissionCriteriaList(
                            (List<AdmissionCriteriaResponse>) admission.getAdmissionCriteriaList()
                                    .stream()
                                    .map(criteria -> {
                                        return   mapCriteriaToCriteriaResponse(criteria);
                                             })
                                    .toList()
                            )
                .applicationCategoryList(
                        (List<ApplicationCategoryResponse>) admission.getApplicationCategoryList()
                                .stream().map(category -> {
                                                return mapCategoryToCategoryResponse(category);
                                             })
                                .toList()
                        )
                .build()
                );
    }


    private static AdmissionCriteriaResponse mapCriteriaToCriteriaResponse(AdmissionCriteria criteria) {
        return AdmissionCriteriaResponse.builder()
                .id(criteria.getIdAdmissionCriteria())
                .criteria(criteria.getCriteria())
                .value(criteria.getValue())
                .build();
    }

    public static AdmissionCriteria mapCriteriaRequestToCriteria(AdmissionCriteriaRequest admissionCriteriaRequest) {
        return AdmissionCriteria.builder()
                .criteria(admissionCriteriaRequest.getCriteria())
                .value(admissionCriteriaRequest.getValue())
                .operand(admissionCriteriaRequest.getOperand())
                .build();
    }

    private static AdmissionCriteria mapCriteriaRequestToCriteria(AdmissionCriteriaRequest admissionCriteriaRequest,AdmissionCriteria a) {
                a.setCriteria(admissionCriteriaRequest.getCriteria());
                a.setValue(admissionCriteriaRequest.getValue());
        return a;
    }
    public static ApplicationCategory mapCategoryRequestToCategory(ApplicationCategoryRequest applicationCategoryRequest) {
        return ApplicationCategory.builder()
                .applicationFormType(applicationCategoryRequest.getApplicationFormType())
                .applicationFormAmount(applicationCategoryRequest.getApplicationFormAmount())
                .applicationFormQNT(applicationCategoryRequest.getApplicationFormQNT())
                .appointmentClosure(LocalDateTime.parse(applicationCategoryRequest.getAppointmentClosure().replace("T", " "),formatter))
                .appointmentCommencement(LocalDateTime.parse(applicationCategoryRequest.getAppointmentCommencement().replace("T", " "),formatter))
                .paymentMedium(applicationCategoryRequest.getPaymentMedium())
                .closure(LocalDateTime.parse(applicationCategoryRequest.getClosure().replace("T", " "),formatter))
                .commencement(LocalDateTime.parse(applicationCategoryRequest.getCommencement().replace("T", " "),formatter))
                .appointmentPerDay(applicationCategoryRequest.getAppointmentPerDay())

                .build();
    }
    private static ApplicationCategory mapCategoryRequestToCategory(ApplicationCategoryRequest applicationCategoryRequest, ApplicationCategory c) {
                c.setApplicationFormType(applicationCategoryRequest.getApplicationFormType());
                c.setApplicationFormAmount(applicationCategoryRequest.getApplicationFormAmount());
                c.setApplicationFormQNT(applicationCategoryRequest.getApplicationFormQNT());
                c.setAppointmentClosure(LocalDateTime.parse(applicationCategoryRequest.getAppointmentClosure(),formatter));
                c.setAppointmentCommencement(LocalDateTime.parse(applicationCategoryRequest.getAppointmentCommencement(),formatter));
                c.setPaymentMedium(applicationCategoryRequest.getPaymentMedium());
                c.setClosure(LocalDateTime.parse(applicationCategoryRequest.getClosure(),formatter));
                c.setCommencement(LocalDateTime.parse(applicationCategoryRequest.getCommencement(),formatter));
                c.setAppointmentPerDay(applicationCategoryRequest.getAppointmentPerDay());
        return c;
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

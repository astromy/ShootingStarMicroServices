package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.DesignationUnitRequest;
import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.DesignationUnitResponse;
import com.astromyllc.shootingstar.hr.model.DesignationUnit;
import com.astromyllc.shootingstar.hr.model.StaffDesignation;
import com.astromyllc.shootingstar.hr.repository.StaffDesignationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffDesignationUnitUtil {
    public static List<StaffDesignation> staffDesignationsGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static Long staffDesignationIndex = 0L;
    private static InstitutionRequest institutionRequest = null;
    private final StaffDesignationRepository staffDesignationRepository;
    private final WebClient.Builder webClientBuilder;
    private final ProfessionalRecordsUtil professionalRecordsUtil;
    private final StaffDocumentsUtil staffDocumentsUtil;
    private final DependantsUtil dependantsUtil;
    private final AcademicRecordsUtil academicRecordsUtil;
    @Value("${gateway.host}")
    private String host;

    public static DesignationUnit mapStaffDesignationUnitRequest_ToStaffDesignationUnit(DesignationUnitRequest a) {
        return DesignationUnit.builder()
                .unitName(a.getUnitName())
                .designation(a.getDesignation())
                .institutionCode(a.getInstitutionCode())
                .build();
    }

    public static DesignationUnitResponse mapStaffDesignationUnit_ToStaffDesignationUnitResponse(DesignationUnit a) {
        return DesignationUnitResponse.builder()
                .unitName(a.getUnitName())
                .designation(a.getDesignation())
                .institutionCode(a.getInstitutionCode())
                .build();
    }

    @PostConstruct
    private void fetchStaffDesignationUnit() {
        staffDesignationsGlobalList = staffDesignationRepository.findAll();
        log.info("{} staff RECORDS FETCHED", staffDesignationsGlobalList.size());
    }

    private String generateApplicationCode(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();
        institutionRequest =

                webClientBuilder
                        .build()
                        .post()
                        .uri(host + "/api/setup/getInstitutionByCode")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(InstitutionRequest.class)
                        .block();
        staffDesignationIndex = staffDesignationsGlobalList
                .stream()
                .filter(x -> x.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .count() + 1;
        return "S" + institutionCode + "-" + staffDesignationIndex;

    }


}

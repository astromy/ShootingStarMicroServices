package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.DesignationUnitRequest;
import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffDesignationRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.DesignationUnitResponse;
import com.astromyllc.shootingstar.hr.model.AcademicRecords;
import com.astromyllc.shootingstar.hr.model.DesignationUnit;
import com.astromyllc.shootingstar.hr.model.StaffDesignation;
import com.astromyllc.shootingstar.hr.repository.StaffDesignationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffDesignationUnitUtil {
    private final StaffDesignationRepository staffDesignationRepository;
    private final WebClient.Builder webClientBuilder;
    public static List<StaffDesignation> staffDesignationsGlobalList;
    private static Long staffDesignationIndex = 0L;
    private static InstitutionRequest institutionRequest = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${gateway.host}")
    private String host;

    private final ProfessionalRecordsUtil professionalRecordsUtil;
    private final StaffDocumentsUtil staffDocumentsUtil;
    private final DependantsUtil dependantsUtil;
    private final AcademicRecordsUtil academicRecordsUtil;


    @Bean
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
                        .baseUrl("http://"+host)
                        .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            System.out.println("Request: " + clientRequest);
                            return Mono.just(clientRequest);
                        }))
                        .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                            System.out.println("Response: " + clientResponse);
                            return Mono.just(clientResponse);
                        }))
                        .build()
                        .post()
                        .uri("/api/setup/getInstitutionByCode")
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


}

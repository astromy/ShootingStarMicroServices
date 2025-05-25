package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.AcademicRecordsRequest;
import com.astromyllc.shootingstar.hr.dto.request.DesignationListRequest;
import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffDesignationRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.DesignationListResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffDesignationResponse;
import com.astromyllc.shootingstar.hr.model.AcademicRecords;
import com.astromyllc.shootingstar.hr.model.DesignationList;
import com.astromyllc.shootingstar.hr.model.StaffDesignation;
import com.astromyllc.shootingstar.hr.repository.DesignationListRepository;
import com.astromyllc.shootingstar.hr.repository.StaffDesignationRepository;
import jakarta.annotation.PostConstruct;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffDesignationUtil {
    private final DesignationListRepository staffDesignationListRepository;
    private final WebClient.Builder webClientBuilder;
    public static List<DesignationList> staffDesignationListGlobalList;
    private static Long staffDesignationListIndex = 0L;
    private static InstitutionRequest institutionRequest = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${gateway.host}")
    private String host;

    private final StaffDesignationUnitUtil staffDesignationUnitUtil;


    @PostConstruct
    private void fetchStaffDesignationList() {
        staffDesignationListGlobalList = staffDesignationListRepository.findAll();
        log.info("{} staff RECORDS FETCHED", staffDesignationListGlobalList.size());
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
        staffDesignationListIndex = staffDesignationListGlobalList
                .stream()
                .filter(x -> x.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .count() + 1;
        return "S" + institutionCode + "-" + staffDesignationListIndex;

    }

    public static DesignationList mapStaffDesignationListRequest_ToStaffDesignationList(DesignationListRequest a) {
        return DesignationList.builder()
                .designation(a.getDesignation())
                .department(a.getDepartment())
                .institutionCode(a.getInstitutionCode())
                .designationUnits(a.getDesignationUnits().stream()
                        .map(StaffDesignationUnitUtil::mapStaffDesignationUnitRequest_ToStaffDesignationUnit).toList())
                .build();
    }

    public static DesignationListResponse mapStaffDesignationList_ToStaffDesignationListResponse(DesignationList a) {
        return DesignationListResponse.builder()
                .designation(a.getDesignation())
                .department(a.getDepartment())
                .institutionCode(a.getInstitutionCode())
                .designationUnits(a.getDesignationUnits().stream()
                        .map(StaffDesignationUnitUtil::mapStaffDesignationUnit_ToStaffDesignationUnitResponse).toList())
                .build();
    }


    public void saveAll(List<DesignationList> sdl) {
        staffDesignationListRepository.saveAll(sdl);
        staffDesignationListGlobalList.addAll(sdl);
    }

    public void updateStaffDesignation(DesignationList existing, DesignationListRequest requestRecord) {
        existing.setDesignation(requestRecord.getDesignation());
        existing.setDepartment(requestRecord.getDepartment());
        existing.setInstitutionCode(requestRecord.getInstitutionCode());
        existing.setDesignationUnits(requestRecord.getDesignationUnits().stream().map(StaffDesignationUnitUtil::mapStaffDesignationUnitRequest_ToStaffDesignationUnit).toList());
    }

}

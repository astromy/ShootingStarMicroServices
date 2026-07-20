package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.DesignationListRequest;
import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.DesignationListResponse;
import com.astromyllc.shootingstar.hr.model.DesignationList;
import com.astromyllc.shootingstar.hr.repository.DesignationListRepository;
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
public class StaffDesignationUtil {
    public static List<DesignationList> staffDesignationListGlobalList;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static Long staffDesignationListIndex = 0L;
    private static InstitutionRequest institutionRequest = null;
    private final DesignationListRepository staffDesignationListRepository;
    private final WebClient.Builder webClientBuilder;
    private final StaffDesignationUnitUtil staffDesignationUnitUtil;
    @Value("${gateway.host}")
    private String host;

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
                        .build()
                        .post()
                        .uri(host + "/api/setup/getInstitutionByCode")
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

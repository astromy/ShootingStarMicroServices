package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.AcademicRecordsRequest;
import com.astromyllc.shootingstar.hr.dto.request.DesignationListRequest;
import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffSubjectsRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffSubjectsResponse;
import com.astromyllc.shootingstar.hr.model.AcademicRecords;
import com.astromyllc.shootingstar.hr.model.DesignationList;
import com.astromyllc.shootingstar.hr.model.StaffSubjects;
import com.astromyllc.shootingstar.hr.repository.StaffSubjectsRepository;
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
public class StaffSubjectsUtil {
    private final StaffSubjectsRepository staffSubjectsRepository;
    private final WebClient.Builder webClientBuilder;
    public static List<StaffSubjects> staffSubjectsGlobalList;
    private static Long staffSubjectsIndex = 0L;
    private static InstitutionRequest institutionRequest = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${gateway.host}")
    private String host;


    @Bean
    private void fetchStaffSubjects() {
        staffSubjectsGlobalList = staffSubjectsRepository.findAll();
        log.info("{} staff subjects RECORDS FETCHED", staffSubjectsGlobalList.size());
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
        staffSubjectsIndex = staffSubjectsGlobalList
                .stream()
                .filter(x -> x.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .count() + 1;
        return "S" + institutionCode + "-" + staffSubjectsIndex;

    }

    public static StaffSubjects mapStaffSubjectRequest_ToStaffSubjects(StaffSubjectsRequest a) {
        return StaffSubjects.builder()
                .subjectClass(a.getSubjectClass())
                .staffId(a.getStaffId())
                .subject(a.getSubject())
                .institutionCode(a.getInstitutionCode())
                .build();
    }

    public static StaffSubjectsResponse mapStaffSubject_ToStaffSubjectsResponse(StaffSubjects a) {
        return StaffSubjectsResponse.builder()
                .subjectClass(a.getSubjectClass())
                .staffId(a.getStaffId())
                .subject(a.getSubject())
                .institutionCode(a.getInstitutionCode())
                .build();
    }


    public void saveAll(List<StaffSubjects> sdl) {
        staffSubjectsRepository.saveAll(sdl);
        staffSubjectsGlobalList.addAll(sdl);
    }

    public static void updateStaffSubjects(StaffSubjects existing, StaffSubjectsRequest requestRecord) {
        existing.setSubjectClass(requestRecord.getSubjectClass());
        existing.setSubject(requestRecord.getSubject());
        existing.setStaffId(requestRecord.getStaffId());
        existing.setInstitutionCode(requestRecord.getInstitutionCode());
    }

}

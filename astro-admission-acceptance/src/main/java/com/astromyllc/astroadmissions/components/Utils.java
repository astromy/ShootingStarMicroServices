package com.astromyllc.astroadmissions.components;

import com.astromyllc.astroadmissions.dto.request.DynamicStringRequest;
import com.astromyllc.astroadmissions.dto.request.Students2Request;
import com.astromyllc.astroadmissions.dto.response.ApplicationsResponse;
import com.astromyllc.astroadmissions.dto.response.InstitutionResponse;
import com.astromyllc.astroadmissions.dto.response.StudentsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Utils {
    private final WebClient.Builder webClientBuilder;
    @Value("${gateway.host}")
    private String host;

    public List<InstitutionResponse> fetchAllInstitutions() {
        return webClientBuilder.build().post()
                .uri(host + "/api/setup/getAllSubscribedInstitution")
                .contentType(MediaType.APPLICATION_JSON)
                //.body(Mono.just(json), JSONObject.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InstitutionResponse>>() {
                }).block();
    }

    public ResponseEntity<String>
    admittedStudent(Students2Request jso) {
        try {
            String responseBody = webClientBuilder.build().post()
                    .uri(host + "/api/administration-pta/accept-admissions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(jso)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(responseBody);

        } catch (WebClientResponseException e) {
            // This catches 4xx and 5xx errors
            return ResponseEntity.status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    public StudentsResponse fetchStudentByID(DynamicStringRequest request) {

        try {
            log.info("Attempting to fetch student from admission microservice: /api/applications/getApplicationByCode");
            ApplicationsResponse appResponse = webClientBuilder.build().post()
                    .uri(host + "/api/applications/getApplicationByCode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        log.warn("Admission microservice returned error {}, will fall back to administration-pta", response.statusCode());
                        return Mono.error(new IllegalStateException("Admission service error: " + response.statusCode()));
                    })
                    .bodyToMono(ApplicationsResponse.class)
                    .block();

            if (appResponse != null && appResponse.getApplicationCode() != null) {
                log.info("Student found via admission service: {}", appResponse.getApplicationCode());
                return appResponse.toStudentsResponse();
            }

            log.warn("Admission microservice returned null/empty for request: {}", request);
        } catch (Exception e) {
            log.warn("Admission microservice lookup failed ({}), falling back to administration-pta", e.getMessage());
        }

        try {
            log.info("Falling back to administration-pta: /api/administration-pta/getStudentsByDynamicData");
            List<StudentsResponse> studentsResponse = webClientBuilder.build().post()
                    .uri(host + "/api/administration-pta/getStudentsByDynamicData")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        log.error("HTTP error occurred on administration-pta: {}", response.statusCode());
                        return Mono.error(new IllegalStateException("Failed to fetch student data: " + response.statusCode()));
                    })
                    .bodyToMono(new ParameterizedTypeReference<List<StudentsResponse>>() {
                    })
                    .block();

            if (studentsResponse == null || studentsResponse.isEmpty()) {
                log.warn("No student data found in either microservice for request: {}", request);
                return null;
            }

            if (studentsResponse.size() > 1) {
                log.debug("Multiple students found ({}), returning first one", studentsResponse.size());
            }

            log.info("Student found via administration-pta microservice");
            return studentsResponse.get(0);

        } catch (Exception e) {
            log.error("Error fetching student data from administration-pta: {}", e.getMessage(), e);
            return null;
        }
    }
}

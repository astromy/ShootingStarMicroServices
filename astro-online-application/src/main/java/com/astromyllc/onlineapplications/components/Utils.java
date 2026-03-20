package com.astromyllc.onlineapplications.components;

import com.astromyllc.onlineapplications.dto.request.DynamicStringRequest;
import com.astromyllc.onlineapplications.dto.request.Students2Request;
import com.astromyllc.onlineapplications.dto.response.InstitutionResponse;
import com.astromyllc.onlineapplications.dto.response.StudentsResponse;
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

    public ResponseEntity<String> admittedStudent(Students2Request jso) {
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
            List<StudentsResponse> studentsResponse = webClientBuilder.build().post()
                    .uri(host + "/api/administration-pta/getStudentsByDynamicData")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        log.error("HTTP error occurred: {}", response.statusCode());
                        return Mono.error(new IllegalStateException("Failed to fetch student data: " + response.statusCode()));
                    })
                    .bodyToMono(new ParameterizedTypeReference<List<StudentsResponse>>() {
                    })
                    .block();

            if (studentsResponse == null || studentsResponse.isEmpty()) {
                log.warn("No student data found for request: {}", request);
                return null; // Return null instead of throwing exception
            }

            if (studentsResponse.size() > 1) {
                log.debug("Multiple students found ({}), returning first one", studentsResponse.size());
            }

            return studentsResponse.get(0);

        } catch (Exception e) {
            log.error("Error fetching student data: {}", e.getMessage(), e);
            return null; // Return null on any error
        }

    }
}

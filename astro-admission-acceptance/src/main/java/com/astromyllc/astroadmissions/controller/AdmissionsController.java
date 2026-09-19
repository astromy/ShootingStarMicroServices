package com.astromyllc.astroadmissions.controller;


import com.astromyllc.astroadmissions.components.Utils;
import com.astromyllc.astroadmissions.dto.request.DynamicStringRequest;
import com.astromyllc.astroadmissions.dto.request.Students2Request;
import com.astromyllc.astroadmissions.dto.response.InstitutionResponse;
import com.astromyllc.astroadmissions.dto.response.StudentsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequiredArgsConstructor
@Slf4j
public class AdmissionsController {

    private final Utils utils;
    private final ObjectMapper objectMapper;
    @Value("${gateway.host}")
    private String backendserve;
    @Value("${orb.uploads.pictures:${orb.uploads.base:static/applicationDocuments}/Pictures}")
    private String picturesDir;
    @Value("${orb.uploads.birthCerts:${orb.uploads.base:static/applicationDocuments}/BirthCerts}")
    private String birthCertsDir;

    @RequestMapping(value = "/fetchAllInstitutions", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<InstitutionResponse>> postBulkStudentList() {
        return ResponseEntity.ok(utils.fetchAllInstitutions());
    }

    //fetchAllInstitutions

    @RequestMapping(value = "/postedStudentRegistration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> postBulkStudentList(@RequestBody Students2Request jso) {
        return utils.admittedStudent(jso);
    }

    @RequestMapping(value = "/fetchStudent", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getStudentByID(@RequestBody DynamicStringRequest jso) {
        StudentsResponse student = utils.fetchStudentByID(jso);

        if (student == null) {
            // Return a different response object or message
            String studentId = "unknown";
            if (jso.getKey() != null && jso.getVal() != null) {
                int index = jso.getKey().indexOf("studId");
                if (index != -1 && index < jso.getVal().size()) {
                    studentId = jso.getVal().get(index);
                }
            }

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Student not found");
            errorResponse.put("studentId", studentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return ResponseEntity.ok(student);
    }

    // ── Applicant documents (streamed from online-application via api-gateway) ──

    @RequestMapping(value = "getApplicantPicture/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getApplicantPicture(@PathVariable String filename) {
        return BACKENDGET_BYTES(
                backendserve + "/api/applications/applicationDocuments/Pictures/" + filename,
                MediaType.IMAGE_PNG
        );
    }

    @RequestMapping(value = "getApplicantBirthCert/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getApplicantBirthCert(@PathVariable String filename) {
        return BACKENDGET_BYTES(
                backendserve + "/api/applications/applicationDocuments/BirthCerts/" + filename,
                MediaType.APPLICATION_PDF
        );
    }

// ── helper for GET → byte[] with correct Content-Type ──

    private ResponseEntity<byte[]> BACKENDGET_BYTES(String url, MediaType mediaType) {
        log.info("Calling API: {}", url);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                log.warn("Backend returned {} for {}", response.statusCode(), url);
                return ResponseEntity.status(response.statusCode()).build();
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")   // so PDFs render in-tab
                    .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                    .body(response.body());

        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch from backend: {} — {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }


    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {

        log.info("Calling API: {} ", url);

        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(jso)))
                    .build();
            log.info("Calling API With REQUEST: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException | InterruptedException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }

}

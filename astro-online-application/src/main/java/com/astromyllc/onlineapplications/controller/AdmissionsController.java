package com.astromyllc.onlineapplications.controller;


import com.astromyllc.onlineapplications.components.Utils;
import com.astromyllc.onlineapplications.dto.request.DynamicStringRequest;
import com.astromyllc.onlineapplications.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.onlineapplications.dto.request.Students2Request;
import com.astromyllc.onlineapplications.dto.response.InstitutionResponse;
import com.astromyllc.onlineapplications.dto.response.StudentsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
    private final RestTemplateBuilder restTemplateBuilder;
    @Value("${gateway.host}")
    private String backendserve;

    @ResponseBody
    //@PostMapping("/preRequestInstitution")
    @RequestMapping(value = "/preRequestInstitution", method = RequestMethod.POST)
    public String preRequestInstitution(@RequestBody PreOrderInstitutionRequest jso) throws IOException {
        // jso.setCreationDate(LocalDate.now());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(jso);
        URL url = new URL(backendserve + "/api/setup/preRequestInstitution");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        String jsonInputString = json;
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());

            return response.toString();
        }
    }


    @RequestMapping(value = "/fetchAllInstitutions", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<InstitutionResponse>> postBulkStudentList() {
        List<InstitutionResponse> temp = utils.fetchAllInstitutions();
        return ResponseEntity.ok(utils.fetchAllInstitutions());
    }


    @RequestMapping(value = "/postedStudentRegistration", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> postBulkStudentList(@RequestBody Students2Request jso) {
        return utils.admittedStudent(jso);
    }


    @RequestMapping(value = "/postedStudentApplication", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> postedStudentApplication(@RequestBody Students2Request jso) {
        log.info("Received application for institution: {}", jso.getInstitutionCode());
        log.info("Picture base64 length: {}",
                jso.getPicture() != null ? jso.getPicture().length() : "null");
        log.info("BirthCert base64 length: {}",
                jso.getBirthCert() != null ? jso.getBirthCert().length() : "null");
        return forwardToMicroservice(jso, backendserve + "/api/applications/submit-application");
    }


    @RequestMapping(value = "/resolveApplicationIssue", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> resolveApplicationIssue(@RequestBody Students2Request jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/applications/resolve");
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

    //fetchAllInstitutions


    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {

        log.info("Calling API: {} ", url);

        try {
            String jsonBody = objectMapper.writeValueAsString(jso);
            log.info("Request body size: {} bytes", jsonBody.length());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofMinutes(3))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(jso)))
                    .build();
            log.info("Calling API With REQUEST: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException | InterruptedException e) {
            log.error("BACKENDCOMMPOST failed for {}: {}", url, e.getMessage());
            log.error(String.valueOf(e));
        }
        return null;
    }

    // NEW METHOD - Replace your old BACKENDCOMMPOST with this
    private ResponseEntity<String> forwardToMicroservice(Object jso, String url) {
        log.info("Forwarding to microservice: {}", url);

        try {
            String jsonBody = objectMapper.writeValueAsString(jso);
            double sizeInMB = jsonBody.length() / 1024.0 / 1024.0;
            log.info("Request body size: {} bytes ({:.2f} MB)", jsonBody.length(), sizeInMB);

            // Log base64 lengths to verify they're not truncated
            if (jso instanceof Students2Request) {
                Students2Request request = (Students2Request) jso;
                log.info("Picture base64 length: {} chars",
                        request.getPicture() != null ? request.getPicture().length() : 0);
                log.info("BirthCert base64 length: {} chars",
                        request.getBirthCert() != null ? request.getBirthCert().length() : 0);
            }

            // Create RestTemplate with large file support
            RestTemplate restTemplate = restTemplateBuilder
                    .setConnectTimeout(Duration.ofSeconds(30))
                    .setReadTimeout(Duration.ofMinutes(5))
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            long startTime = System.currentTimeMillis();
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            long endTime = System.currentTimeMillis();

            log.info("Microservice response time: {} ms", endTime - startTime);
            log.info("Response status: {}", response.getStatusCode());

            return response;

        } catch (Exception e) {
            log.error("Failed to forward to microservice at {}: {}", url, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error forwarding to microservice: " + e.getMessage());
        }
    }
}


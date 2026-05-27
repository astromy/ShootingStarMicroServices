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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
        return BACKENDCOMMPOST(jso, backendserve + "/api/applications/submit-application");
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

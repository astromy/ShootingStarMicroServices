package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.authorization.PermissionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
@ResponseBody
@RequiredArgsConstructor
@Slf4j
public class SetupController {

    @Value("${gateway.host}")
    private String backendserve;


    @ResponseBody
    @RequestMapping(value = "/preRequestInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> preRequestInstitution(@RequestBody PreOrderInstitutionRequest jso) throws IOException, InterruptedException {
        ResponseEntity<String> responseData = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/signupInstitution");
        //log.error("PreOrderInstitution Feed from Setup  \n ==> {}", responseData);
        return responseData;
    }

    @ResponseBody
    //@PostMapping("/migratePreOrder")
    @RequestMapping(value = "/migratePreOrder", method = RequestMethod.POST)
    public ResponseEntity<String> migratePreorder(@RequestBody String jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST MIGRATION OF..... {}", jso);
        ResponseEntity<String> responseData = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/migratePreOrder");
       // log.error("Migration Feed from Setup  \n ==> {}", responseData);
        return responseData;
    }

    @ResponseBody
    @RequestMapping(value = "/addLookUps", method = RequestMethod.POST)
    public ResponseEntity<String> addLookUp(@RequestBody List<LookupRequest> jso) throws IOException {
        log.error("\n\n\n\n REQUEST ADDING LOOKUP OF..... {}", jso);
        ResponseEntity<String> responseData = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), "http://" + backendserve + "/api/setup/addLookUps");
        //log.error("Adding Lookup Feed from Setup  \n ==> {}", responseData);
        return responseData;
    }

    @ResponseBody
    @RequestMapping(value = "/addClasses", method = RequestMethod.POST)
    public ResponseEntity<String> addClasses(@RequestBody ClassesRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING CLASSES OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addClasses");
        //log.error("Adding Classes Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/addSubjects", method = RequestMethod.POST)
    public ResponseEntity<String> addSubjects(@RequestBody SubjectRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING SUBJECTS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addSubjects");
       // log.error("Adding Subjects Feed from Setup  \n ==> {}", response);
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/addAdmissions", method = RequestMethod.POST)
    public ResponseEntity<String> addAdmissions(@RequestBody AdmissionsEntryRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING ADMISSIONS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addAdmissionSetup");
       // log.error("Adding Admissions Feed from Setup  \n ==> {}", response);
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/addDepartment", method = RequestMethod.POST)
    public ResponseEntity<String> addDepartments(@RequestBody DepartmentRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING DEPARTMENT OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addDepartment");
        //log.error("Adding Department Feed from Setup  \n ==> {}", response);
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/addGradingSetting", method = RequestMethod.POST)
    public ResponseEntity<String> addGrading(@RequestBody GradingSettingRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING GRADING SETTINGS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addGradingSetting");
       // log.error("Adding Grading Setting Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/addPermissions", method = RequestMethod.POST)
    public ResponseEntity<String> addPermissions(@RequestBody PermissionRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING PERMISSIONS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addLookUp");
        //log.error("Adding Permissions Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/addDesignation", method = RequestMethod.POST)
    public ResponseEntity<String> addDesignation(@RequestBody DesignationRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST ADDING DESIGNATIONS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addDesignations");
        //log.error("Adding Designation Feed from Setup  \n ==> {}", response);
        return response;
    }


    //=======================================================================================================================
    @ResponseBody
    @RequestMapping(value = "/getInstitutionByCode", method = RequestMethod.POST)
    public ResponseEntity<String> fetchInstitution(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET INSTITUTION BY CODE OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionByCode");
        //log.error("Institution Feed from Setup  \n ==> {}", response);
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/getLookUpByType", method = RequestMethod.POST)
    public ResponseEntity<String> getLookUpByType(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET LOOKUP BY TYPE OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getLookUpByType");
        //log.error("Lookup Feed from Setup  \n ==> {},{}", response.getHeaders(), response.getBody());
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionClasses", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionClasses(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET INSTITUTION CLASSES OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionClasses");
        //log.error("Classes Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionClassesByClassGroup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionClassesByClassGroup(@RequestBody ClassGroupRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET CLASSES BY CLASS GROUP OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionClassesByClassGroup");
        //log.error("Class Group Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionSubjects", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionSubjects(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET SUBJECTS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionSubjects");
        //log.error("Subjects Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionSubjectsAndClassGroup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionSubjects(@RequestBody SubjectDetails jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET SUBJECTS BY CLASS GROUP OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionSubjectsAndClassGroup");
        //log.error("Subjects Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionAdmissionSetup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionAdmissionSetup(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET ADMISSION SETUP OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionAdmissionSetup");
        //log.error("Admissions Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionDepartment", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionDepartment(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET DEPARTMENT OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionDepartment");
        //log.error("Department Feed from Setup  \n ==> {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionGradingSetting", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionGrading(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET GRADING SETTINGS OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionGradingSetting");
        //log.error("Grading Feed from Setup==> \n {}", response);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getDesignation", method = RequestMethod.POST)
    public ResponseEntity<String> getDesignation(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        log.error("\n\n\n\n REQUEST GET DESIGNATION OF..... {}", jso);
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionDesignations");
        //log.error("Designation Feed from Setup==>  \n {}", response);
        return response;
    }


    private ResponseEntity<String> BACKENDCOMMPOSTLIST(List<Object> jso, String url) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder response = new StringBuilder();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        BufferedReader br = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            String json = ow.writeValueAsString(jso);
            json = json.substring(1, json.length() - 1);
            wr.write(json.getBytes(StandardCharsets.UTF_8));
            wr.flush();
            wr.close();

            InputStream inputStream;

            int status = httpURLConnection.getResponseCode();

            if (status >= 200 && status < 300) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            try (BufferedReader brIn = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"))) {
                String responseLine = null;
                while ((responseLine = brIn.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // System.out.println(response.toString());
            return ResponseEntity.ok(response.toString());
        } catch (IOException e) {
            // Log the error for errorging
            e.printStackTrace();
            // Return a generic error response with status 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching institution: " + e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {
        log.info("Calling API: {}", url);


        try {
            // 4. Create the request with proper authorization header
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(jso)))
                    .build();


            // 5. Execute and handle response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 401) {
                log.error("Backend rejected token. Status: {} - Body: {}", response.statusCode(), response.body());
            }

            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception e) {
            log.error("API call failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

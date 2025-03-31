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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    /*@Autowired
    private OAuth2AuthorizedClientService authorizedClientService;*/

    @ResponseBody
    @RequestMapping(value = "/preRequestInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> preRequestInstitution(@RequestBody PreOrderInstitutionRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/signupInstitution");
    }

    @ResponseBody
    //@PostMapping("/migratePreOrder")
    @RequestMapping(value = "/migratePreOrder", method = RequestMethod.POST)
    public ResponseEntity<String> migratePreorder(@RequestBody String jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/migratePreOrder");
    }

    @ResponseBody
    @RequestMapping(value = "/addLookUps", method = RequestMethod.POST)
    public ResponseEntity<String> addLookUp(@RequestBody List<LookupRequest> jso) throws IOException {
        return BACKENDCOMMPOSTLIST(Collections.singletonList(jso), "http://" + backendserve + "/api/setup/addLookUps");
    }

    @ResponseBody
    @RequestMapping(value = "/addClasses", method = RequestMethod.POST)
    public ResponseEntity<String> addClasses(@RequestBody ClassesRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addClasses");
    }

    @ResponseBody
    @RequestMapping(value = "/addSubjects", method = RequestMethod.POST)
    public ResponseEntity<String>  addSubjects(@RequestBody SubjectRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addSubjects");
    }


    @ResponseBody
    @RequestMapping(value = "/addAdmissions", method = RequestMethod.POST)
    public ResponseEntity<String> addAdmissions(@RequestBody AdmissionsEntryRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addAdmissionSetup");
    }


    @ResponseBody
    @RequestMapping(value = "/addDepartment", method = RequestMethod.POST)
    public ResponseEntity<String>  addDepartments(@RequestBody DepartmentRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addDepartment");
    }


    @ResponseBody
    @RequestMapping(value = "/addGradingSetting", method = RequestMethod.POST)
    public ResponseEntity<String> addGrading(@RequestBody GradingSettingRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addGradingSetting");
    }

    @ResponseBody
    @RequestMapping(value = "/addPermissions", method = RequestMethod.POST)
    public ResponseEntity<String> addPermissions(@RequestBody PermissionRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addLookUp");
    }

    @ResponseBody
    @RequestMapping(value = "/addDesignation", method = RequestMethod.POST)
    public ResponseEntity<String> addDesignation(@RequestBody DesignationRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addDesignations");
    }





//=======================================================================================================================
    @ResponseBody
    @RequestMapping(value = "/getInstitutionByCode", method = RequestMethod.POST)
    public ResponseEntity<String> fetchInstitution(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionByCode");
    }


    @ResponseBody
    @RequestMapping(value = "/getLookUpByType", method = RequestMethod.POST)
    public ResponseEntity<String> getLookUpByType(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getLookUpByType");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionClasses", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionClasses(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionClasses");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionClassesByClassGroup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionClassesByClassGroup(@RequestBody ClassGroupRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionClassesByClassGroup");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionSubjects", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionSubjects(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionSubjects");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionSubjectsAndClassGroup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionSubjects(@RequestBody SubjectDetails jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionSubjectsAndClassGroup");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionAdmissionSetup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionAdmissionSetup(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionAdmissionSetup");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionDepartment", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionDepartment(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionDepartment");
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionGradingSetting", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionGrading(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionGradingSetting");
    }

    @ResponseBody
    @RequestMapping(value = "/getDesignation", method = RequestMethod.POST)
    public ResponseEntity<String> getDesignation(@RequestBody SingleStringRequest jso) throws IOException, InterruptedException {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionDesignations");
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
            // Log the error for debugging
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

    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(jso)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return ResponseEntity.status(response.statusCode()).body(response.body());
    }

}

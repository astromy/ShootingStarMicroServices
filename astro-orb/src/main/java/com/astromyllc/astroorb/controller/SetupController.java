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
    public ResponseEntity<String> preRequestInstitution(@RequestBody PreOrderInstitutionRequest jso) throws IOException {

        ResponseEntity<String> responseData = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/signupInstitution");
        return responseData;
    }

    @ResponseBody
    //@PostMapping("/migratePreOrder")
    @RequestMapping(value = "/migratePreOrder", method = RequestMethod.POST)
    public ResponseEntity<String> migratePreorder(@RequestBody String jso) throws IOException {

        ResponseEntity<String> responseData = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/migratePreOrder");
        return responseData;
    }

    @ResponseBody
    @RequestMapping(value = "/addLookUps", method = RequestMethod.POST)
    public ResponseEntity<String> addLookUp(@RequestBody List<LookupRequest> jso) throws IOException {

        ResponseEntity<String> responseData = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), "http://" + backendserve + "/api/setup/addLookUps");
        return responseData;
    }

    @ResponseBody
    @RequestMapping(value = "/addClasses", method = RequestMethod.POST)
    public ResponseEntity<String> addClasses(@RequestBody ClassesRequest jso) throws IOException {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addClasses");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/addSubjects", method = RequestMethod.POST)
    public ResponseEntity<String>  addSubjects(@RequestBody SubjectRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addSubjects");
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/addAdmissions", method = RequestMethod.POST)
    public ResponseEntity<String> addAdmissions(@RequestBody AdmissionsEntryRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addAdmissionSetup");
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/addDepartment", method = RequestMethod.POST)
    public ResponseEntity<String>  addDepartments(@RequestBody DepartmentRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addDepartment");
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/addGradingSetting", method = RequestMethod.POST)
    public ResponseEntity<String> addGrading(@RequestBody GradingSettingRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addGradingSetting");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/addPermissions", method = RequestMethod.POST)
    public ResponseEntity<String> addPermissions(@RequestBody PermissionRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addLookUp");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/addDesignation", method = RequestMethod.POST)
    public ResponseEntity<String> addDesignation(@RequestBody DesignationRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/addDesignations");
        return response;
    }





//=======================================================================================================================
    @ResponseBody
    @RequestMapping(value = "/getInstitutionByCode", method = RequestMethod.POST)
    public ResponseEntity<String> fetchInstitution(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionByCode");
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "/getLookUpByType", method = RequestMethod.POST)
    public ResponseEntity<String> getLookUpByType(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getLookUpByType");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionClasses", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionClasses(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionClasses");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionClassesByClassGroup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionClassesByClassGroup(@RequestBody ClassGroupRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionClassesByClassGroup");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionSubjects", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionSubjects(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionSubjects");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionSubjectsAndClassGroup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionSubjects(@RequestBody SubjectDetails jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionSubjectsAndClassGroup");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionAdmissionSetup", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionAdmissionSetup(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionAdmissionSetup");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionDepartment", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionDepartment(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionDepartment");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getInstitutionGradingSetting", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionGrading(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionGradingSetting");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/getDesignation", method = RequestMethod.POST)
    public ResponseEntity<String> getDesignation(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/getInstitutionDesignations");
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

            if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_ACCEPTED && status != HttpURLConnection.HTTP_CREATED && status != HttpURLConnection.HTTP_NO_CONTENT)
                inputStream = httpURLConnection.getErrorStream();
            else
                inputStream = httpURLConnection.getInputStream();
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

    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder response = new StringBuilder();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        BufferedReader br = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            String json = ow.writeValueAsString(jso);
            wr.writeBytes(json);
            wr.flush();
            wr.close();

            InputStream inputStream;

            int status = httpURLConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK && !String.valueOf(status).contains("2"))
                inputStream = httpURLConnection.getErrorStream();
            else
                inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            try (BufferedReader brIn = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
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

}

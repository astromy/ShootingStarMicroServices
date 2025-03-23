package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class AdministrationController {

    @Value("${gateway.host}")
    private String backendserve;

    @ResponseBody
    @RequestMapping(value = "/conduct-admissions", method = RequestMethod.POST)
    public ResponseEntity<String>conductAdmissions (@RequestBody List<BillRequest> jso) throws IOException {

        return BACKENDCOMMPOSTLIST(Collections.singletonList(jso), "http://" + backendserve + "/api/administration-pta/conduct-admissions");
    }

    @ResponseBody
    @RequestMapping(value = "/getAllStudents", method = RequestMethod.POST)
    public ResponseEntity<String> getAllStudents(@RequestBody SingleStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getAllStudents");
    }

    @ResponseBody
    @RequestMapping(value = "/getStudentsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> getAllStudentsByInstitutionCode(@RequestBody SingleStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getStudentsByInstitution");
    }

    @ResponseBody
    @RequestMapping(value = "/getAssessmentList", method = RequestMethod.POST)
    public ResponseEntity<String> getAssessmentList(@RequestBody ClassListRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getAssessmentList");
    }

    @ResponseBody
    @RequestMapping(value = "/getStudentsByClass", method = RequestMethod.POST)
    public ResponseEntity<String> getStudentsByClass(@RequestBody StudentSkimRequest jso) {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getStudentsByClass");
    }

    @ResponseBody
    @RequestMapping(value = "/getSkimpStudentsByClass", method = RequestMethod.POST)
    public ResponseEntity<String> getSkimpStudentsByClass(@RequestBody StudentSkimRequest jso) {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getSkimpStudentsByClass");
    }

    @ResponseBody
    @RequestMapping(value = "/postBulkStudentList", method = RequestMethod.POST)
    public ResponseEntity<String> submitBulkStudentList(@RequestBody List<StudentsImportRequest> jso) {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/postBulkStudentList");
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

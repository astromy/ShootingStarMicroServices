package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class FinanceController {

    @Value("${gateway.host}")
    private String backendserve;


    @ResponseBody
    @RequestMapping(value = "/create-bills", method = RequestMethod.POST)
    public ResponseEntity<String>addfinance (@RequestBody List<BillRequest> jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), "http://" + backendserve + "/api/finance/create-bills");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/get-bills-by-institution", method = RequestMethod.POST)
    public ResponseEntity<String> getFinanceInstitutionByCode(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/finance/get-bills-by-institution");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/get-billing-by-institutionClass", method = RequestMethod.POST)
    public ResponseEntity<String> getClassBilling(@RequestBody BillingFetchRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/finance/getStudentBillsByInstitutionClass");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "/bill-students-by-institution", method = RequestMethod.POST)
    public ResponseEntity<String> billStudents(@RequestBody BillingsRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/finance/create-billings");
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

        log.info("Calling API: {}", url);
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(jso)))
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

package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.BillRequest;
import com.astromyllc.astroorb.dto.request.BillingFetchRequest;
import com.astromyllc.astroorb.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.astroorb.dto.request.SingleStringRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class FinanceController {

    @Value("${gateway.host}")
    private String backendserve;

    /*@Autowired
    private OAuth2AuthorizedClientService authorizedClientService;*/

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
    @RequestMapping(value = "/get-billing-by-institution", method = RequestMethod.POST)
    public ResponseEntity<String> getClassBilling(@RequestBody BillingFetchRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/finance/get-billings-by-institution");
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

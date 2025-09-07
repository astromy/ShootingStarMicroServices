package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class AdministrationController {
    private final ObjectMapper objectMapper;
    @Value("${gateway.host}")
    private String backendserve;

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        Map<String, String> body = new HashMap<>();
        body.put("csrfToken", token.getToken());
        return ResponseEntity.ok()
                .header("X-XSRF-TOKEN", token.getToken())
                .body(body);
    }

    @ResponseBody
    @RequestMapping(value = "/conduct-admissions", method = RequestMethod.POST)
    public ResponseEntity<String> conductAdmissions(@RequestBody List<BillRequest> jso) throws IOException {

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
    @RequestMapping(value = "/getStudentsByDynamicData", method = RequestMethod.POST)
    public ResponseEntity<String> getAllStudentsByDynamic(@RequestBody DynamicStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getStudentsByDynamicData");
    }

    @ResponseBody
    @RequestMapping(value = "/getAssessmentList", method = RequestMethod.POST)
    public ResponseEntity<String> getAssessmentList(@RequestBody ClassListRequest jso) throws IOException {

        ResponseEntity<String> result = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getAssessmentList");
        return result;
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

    @ResponseBody
    @RequestMapping(value = "api/mobile/getSkimpStudentsByParentContact", method = RequestMethod.POST)
    public ResponseEntity<String> getSkimpStudentsByParentContact(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response= BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/getSkimpStudentsByParentContact");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/sendReactivationEmail", method = RequestMethod.POST)
    public ResponseEntity<String> sendReactivationEmail(@RequestBody DynamicStringRequest jso) {
        return BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/sendReactivationEmail");
    }

    @ResponseBody
    @PostMapping(value = "webhook/subscriptionPaymentStatus")
    public ResponseEntity<String> subscriptionPaymentStatus(HttpServletRequest request, @RequestHeader("x-paystack-signature") String paystackSignature) throws IOException {
        String requestBody = request.getReader().lines().collect(Collectors.joining());

        if (!verifyPaystackSignature(requestBody, paystackSignature)) {
            log.error("Invalid webhook signature. Potential malicious request.");
            return ResponseEntity.status(401).body("Invalid signature");
        }

        PaystackPaymentResponse jso;
        try {
            jso = objectMapper.readValue(requestBody, PaystackPaymentResponse.class);
            log.info("Paystack Response  === {}", jso);
            if (jso.getEvent().equalsIgnoreCase("charge.success")) {
                ResponseEntity<String> serviceResponse=null;
                if (jso.getData().getMetadata().getCustomFields().get(0).getDisplayName().toLowerCase().contains("student id")) {
                    serviceResponse = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/administration-pta/subscriptionPaymentStatus");
                } else {
                    serviceResponse = BACKENDCOMMPOST(jso, "http://" + backendserve + "/api/setup/reactivateInstitutionalAccount");
                }
                return serviceResponse;
            }else{
                return ResponseEntity.status(601).body("Failed Transaction");
            }
        } catch (Exception e) {
            log.error("Error parsing webhook JSON", e);
            return ResponseEntity.badRequest().body("Bad JSON");
        }
    }

    private boolean verifyPaystackSignature(String requestBody, String signature) {
        ;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(PAYSTACK_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hex = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));
            String computedSignature = bytesToHex(hex);
            return computedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying signature", e);
        }
        return false;
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

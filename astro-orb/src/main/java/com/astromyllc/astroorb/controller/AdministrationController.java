package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class AdministrationController {
    private final ObjectMapper objectMapper;
    @Value("${gateway.host}")
    private String backendserve;
    @Value("${paystack.secrete}")
    private String PAYSTACK_SECRET_KEY;

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
    @RequestMapping(value = "conduct-admissions", method = RequestMethod.POST)
    public ResponseEntity<String> conductAdmissions(@RequestBody List<BillRequest> jso) throws IOException {

        return BACKENDCOMMPOSTLIST(Collections.singletonList(jso), backendserve + "/api/administration-pta/conduct-admissions");
    }

    @ResponseBody
    @RequestMapping(value = "getAllStudents", method = RequestMethod.POST)
    public ResponseEntity<String> getAllStudents(@RequestBody SingleStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getAllStudents");
    }

    @ResponseBody
    @RequestMapping(value = "getStudentsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> getAllStudentsByInstitutionCode(@RequestBody SingleStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getStudentsByInstitution");
    }

    @ResponseBody
    @RequestMapping(value = "getStudentsByDynamicData", method = RequestMethod.POST)
    public ResponseEntity<String> getAllStudentsByDynamic(@RequestBody DynamicStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getStudentsByDynamicData");
    }

    @ResponseBody
    @RequestMapping(value = "getAssessmentList", method = RequestMethod.POST)
    public ResponseEntity<String> getAssessmentList(@RequestBody ClassListRequest jso) throws IOException {

        ResponseEntity<String> result = BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getAssessmentList");
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "getStudentsByClass", method = RequestMethod.POST)
    public ResponseEntity<String> getStudentsByClass(@RequestBody DynamicStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getStudentsByClass");
    }

    @ResponseBody
    @RequestMapping(value = "getSkimpStudentsByClass", method = RequestMethod.POST)
    public ResponseEntity<String> getSkimpStudentsByClass(@RequestBody StudentSkimRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getSkimpStudentsByClass");
    }

    @ResponseBody
    @RequestMapping(value = "postBulkStudentList", method = RequestMethod.POST)
    public ResponseEntity<String> submitBulkStudentList(@RequestBody List<StudentsImportRequest> jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/postBulkStudentList");
    }

    @ResponseBody
    @RequestMapping(value = "updateStudentRecord", method = RequestMethod.POST)
    public ResponseEntity<String> updateStudentRecord(@RequestBody StudentsImportRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/updateStudentRecord");
    }

    //------------------------------------------ APPLICANT SECTION ---------------------------------------------------------------------------


    @ResponseBody
    @RequestMapping(value = "getAllApplicants", method = RequestMethod.POST)
    public ResponseEntity<String> getAllApplicants(@RequestBody SingleStringRequest jso) throws IOException {

        return BACKENDCOMMPOST(jso, backendserve + "/api/applications/getApplicationsBySchool");
    }

    @ResponseBody
    @RequestMapping(value = "updateApplicationStatus", method = RequestMethod.POST)
    public ResponseEntity<String> updateApplicationStatus(@RequestBody ArrayList<ApplicantStudentSkimRequest> jso) throws IOException {

        return BACKENDCOMMPOST(jso, backendserve + "/api/applications/updateApplicationList");
    }

    @RequestMapping(value = "getApplicantPicture/{filename}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getApplicantPicture(@PathVariable String filename) {
        return BACKENDCOMMGET(
                backendserve + "/api/applications/applicationDocuments/Pictures/" + filename,
                MediaType.IMAGE_PNG
        );
    }

    @RequestMapping(value = "getApplicantBirthCert/{filename}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getApplicantBirthCert(
            @PathVariable String filename) throws IOException {
        return BACKENDRESGET(
                backendserve + "/api/applications/applicationDocuments/BirthCerts/" + filename,
                MediaType.APPLICATION_PDF
        );
    }


    //----------------------------------------- MOBILE SECTION---------------------------------------------------------------------------

    @ResponseBody
    @RequestMapping(value = "api/mobile/getSkimpStudentsByParentContact", method = RequestMethod.POST)
    public ResponseEntity<String> getSkimpStudentsByParentContact(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getSkimpStudentsByParentContact");
        return response;
    }


    @ResponseBody
    @RequestMapping(value = "api/mobile/updateStudentProfile", method = RequestMethod.POST)
    public ResponseEntity<String> updateStudentProfile(@RequestBody StudentsImportRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/updateStudentRecord");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/getStudentsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> getSkimpStudentsStudentsByInstitution(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getSkimpStudentsByParentContact");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/getStudentsStatusByID", method = RequestMethod.POST)
    public ResponseEntity<String> getStudentsStatusByID(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/checkStudentByID");
        return response;
    }

    // NOTE: "/api/administration-pta/recordGateEvent" is an assumed endpoint
    // name/payload — no administration-pta source has been shared yet, so
    // this hasn't been confirmed against real backend code the way
    // getStudentsStatusByID above has. Confirm the real contract there and
    // adjust if needed.
    @ResponseBody
    @RequestMapping(value = "api/mobile/gateCheck", method = RequestMethod.POST)
    public ResponseEntity<String> gateCheck(@RequestBody GateCheckRequest jso) {
        log.info("REQUEST gateCheck OF..... {}", jso);
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/recordGateEvent");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/busBoarding", method = RequestMethod.POST)
    public ResponseEntity<String> busBoarding(@RequestBody BusBoardingRequest jso) {
        log.info("REQUEST busBoarding OF..... {}", jso);
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/recordBusBoardingEvent");
    }

    // Called from the Academix app when a parent/student self-selects a
    // route — not currently called from Pulse, but exposed the same way
    // since astro-orb is the shared BFF for both.
    @ResponseBody
    @RequestMapping(value = "api/mobile/setStudentRoute", method = RequestMethod.POST)
    public ResponseEntity<String> setStudentRoute(@RequestBody SetStudentRouteRequest jso) {
        log.info("REQUEST setStudentRoute OF..... {}", jso);
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/setStudentRoute");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/sendReactivationEmail", method = RequestMethod.POST)
    public ResponseEntity<String> sendReactivationEmail(@RequestBody DynamicStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/sendReactivationEmail");
    }

    // NOTE (fixed): all four of these were previously wired to
    // "/api/administration-pta/sendReactivationEmail" — a copy-paste error
    // where the target URL was never changed from whatever template this
    // was cloned from. Each now points to its own real endpoint, and each
    // takes a proper typed DTO instead of the generic DynamicStringRequest
    // that was standing in for a real request shape.
    // The endpoint Academix is actually calling. Now takes recipientContact
    // too, so administration-pta can compute each item's `read` state
    // against that specific parent's read receipts.
    @ResponseBody
    @RequestMapping(value = "api/mobile/getNotifications", method = RequestMethod.POST)
    //public ResponseEntity<String> getNotifications(@RequestBody GetNotificationsRequest jso) {
    public ResponseEntity<String> getNotifications(@RequestBody SingleStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getNotifications");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/markNotificationRead", method = RequestMethod.POST)
    public ResponseEntity<String> markNotificationRead(@RequestBody MarkNotificationReadRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/markNotificationRead");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/sendVoiceMessage", method = RequestMethod.POST)
    public ResponseEntity<String> sendVoiceMessage(@RequestBody VoiceMessageRequest jso) {
        log.info("REQUEST sendVoiceMessage OF..... institution={} sentBy={} durationSeconds={}",
                jso.getInstitutionCode(), jso.getSentBy(), jso.getDurationSeconds());
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/sendVoiceMessage");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/getSentVoiceMessages", method = RequestMethod.POST)
    public ResponseEntity<String> getSentVoiceMessages(@RequestBody SingleStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getSentVoiceMessages");
    }

    // Audio bytes are deliberately NOT part of getSentVoiceMessages' response
    // (that stays metadata-only so listing messages is cheap) — fetch this
    // separately, per message, only when something is actually being played.
    @ResponseBody
    @RequestMapping(value = "api/mobile/getVoiceMessageAudio", method = RequestMethod.POST)
    public ResponseEntity<String> getVoiceMessageAudio(@RequestBody SingleStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getVoiceMessageAudio");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/markVoiceMessageListened", method = RequestMethod.POST)
    public ResponseEntity<String> markVoiceMessageListened(@RequestBody MarkVoiceMessageListenedRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/markVoiceMessageListened");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/deleteVoiceMessage", method = RequestMethod.POST)
    public ResponseEntity<String> deleteVoiceMessage(@RequestBody SingleStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/deleteVoiceMessage");
    }

    // Text-based announcements — visible to whichever parents fall under
    // targetClassIds (empty/omitted = every parent at the institution).
    @ResponseBody
    @RequestMapping(value = "api/mobile/sendAnnouncement", method = RequestMethod.POST)
    public ResponseEntity<String> sendAnnouncement(@RequestBody AnnouncementRequest jso) {
        log.info("REQUEST sendAnnouncement OF..... {}", jso);
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/sendAnnouncement");
    }

    // High-priority, school-wide alert. adminpta forces targetClassIds to
    // empty (everyone) server-side regardless of what's sent here — see
    // AnnouncementService.sendEmergencyAlert in that service.
    @ResponseBody
    @RequestMapping(value = "api/mobile/sendEmergencyAlert", method = RequestMethod.POST)
    public ResponseEntity<String> sendEmergencyAlert(@RequestBody AnnouncementRequest jso) {
        log.warn("REQUEST sendEmergencyAlert OF..... {}", jso);
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/sendEmergencyAlert");
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/getAnnouncements", method = RequestMethod.POST)
    public ResponseEntity<String> getAnnouncements(@RequestBody SingleStringRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/getAnnouncements");
    }

    @PostMapping("verify-payment")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> body) {
        String reference = body.get("reference");
        String studentId = body.get("studentId");

        try {
            // 1. Confirm with Paystack directly (fast, doesn't wait on webhook)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.paystack.co/transaction/verify/" + reference))
                    .header("Authorization", "Bearer " + PAYSTACK_SECRET_KEY)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> paystackResult = objectMapper.readValue(response.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) paystackResult.get("data");
            boolean paystackConfirmed = "success".equals(data.get("status"));

            Map<String, Object> result = new HashMap<>();
            if (!paystackConfirmed) {
                result.put("success", false);
                result.put("message", "Payment not confirmed by Paystack");
                return ResponseEntity.ok(result);
            }

            // 2. Check with adminpta whether the account is now active
            //    (the webhook should have already done the activation by the time
            //    this call happens, or you poll briefly / adminpta can activate
            //    idempotently here too if the webhook hasn't landed yet)
            ResponseEntity<String> statusCheck = BACKENDCOMMPOST(
                    Map.of("studentId", studentId),
                    backendserve + "/api/administration-pta/checkStudentByID"
            );

            result.put("success", statusCheck.getStatusCode().is2xxSuccessful());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Payment verification error", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
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
                ResponseEntity<String> serviceResponse = null;
                if (jso.getData().getMetadata().getCustomFields().get(0).getDisplayName().toLowerCase().contains("student id")) {
                    serviceResponse = BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/subscriptionPaymentStatus");
                } else if (jso.getData().getReference().contains("APPLICANT_")) {
                    serviceResponse = BACKENDCOMMPOST(jso, backendserve + "/api/administration-pta/subscriptionPaymentStatus");
                } else {
                    serviceResponse = BACKENDCOMMPOST(jso, backendserve + "/api/setup/reactivateInstitutionalAccount");
                }
                return serviceResponse;
            } else {
                return ResponseEntity.status(601).body("Failed Transaction");
            }
        } catch (Exception e) {
            log.error("Error parsing webhook JSON", e);
            return ResponseEntity.badRequest().body("Bad JSON");
        }
    }

    private boolean verifyPaystackSignature(String requestBody, String signature) {
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

    private ResponseEntity<String> BACKENDCOMMGET(String url) {

        log.info("Calling API: {}", url);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            log.info("Calling API With REQUEST: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());

        } catch (IOException | InterruptedException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }

    private ResponseEntity<byte[]> BACKENDCOMMGET(String url, MediaType mediaType) {
        log.info("Calling API: {}", url);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return ResponseEntity.status(response.statusCode())
                    .contentType(mediaType)
                    .body(response.body());
        } catch (IOException | InterruptedException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }

    private ResponseEntity<byte[]> BACKENDRESGET(String url, MediaType mediaType) {
        log.info("Calling API: {}", url);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<byte[]> response = client.send(
                    request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() != 200) {
                log.error("Backend returned {} for URL: {}", response.statusCode(), url);
                return ResponseEntity.status(response.statusCode()).build();
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(response.body());

        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch from backend: {} — {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

}
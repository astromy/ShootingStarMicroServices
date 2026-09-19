package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.subscription.RequiresPlan;
import com.astromyllc.astroorb.subscription.SubscriptionPlan;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Proxy routes for the Clinic module — backs scripts/subscripts/clinic.js.
 *
 * Follows the exact post() proxy pattern used by StoresController/
 * FinanceController, forwarding to the clinic microservice at /api/clinic/**.
 */
@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
@RequiresPlan(SubscriptionPlan.GROWTH)
public class ClinicController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gateway.host}")
    private String backendserve;

    // ── HELPER ───────────────────────────────────────────────────────────
    private ResponseEntity<String> post(Object body, String url) {
        log.info("Clinic proxy → {}", url);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Clinic proxy call failed: {}", url, e);
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("{\"message\":\"Clinic service unreachable\"}");
        }
    }

    // ── VISITS ───────────────────────────────────────────────────────────
    @RequestMapping(value = "clinic/recordVisit", method = RequestMethod.POST)
    public ResponseEntity<String> recordVisit(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/recordVisit");
    }

    @RequestMapping(value = "clinic/updateVisitClinicalNotes/{visitId}", method = RequestMethod.POST)
    public ResponseEntity<String> updateVisitClinicalNotes(@PathVariable Long visitId, @RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/updateVisitClinicalNotes/" + visitId);
    }

    @RequestMapping(value = "clinic/dischargeVisit/{visitId}", method = RequestMethod.POST)
    public ResponseEntity<String> dischargeVisit(@PathVariable Long visitId, @RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/dischargeVisit/" + visitId);
    }

    @RequestMapping(value = "clinic/getVisitsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> getVisitsByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/getVisitsByInstitution");
    }

    @RequestMapping(value = "clinic/getVisitsByPatient", method = RequestMethod.POST)
    public ResponseEntity<String> getVisitsByPatient(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/getVisitsByPatient");
    }

    // ── PATIENT HISTORY (diagnosis / prescription / vitals) ────────────────
    @RequestMapping(value = "clinic/getDiagnosisByPatient", method = RequestMethod.POST)
    public ResponseEntity<String> getDiagnosisByPatient(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/getDiagnosisByPatient");
    }

    @RequestMapping(value = "clinic/getPrescriptionByPatient", method = RequestMethod.POST)
    public ResponseEntity<String> getPrescriptionByPatient(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/getPrescriptionByPatient");
    }

    @RequestMapping(value = "clinic/getVitalRecordsByPatient", method = RequestMethod.POST)
    public ResponseEntity<String> getVitalRecordsByPatient(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/getVitalRecordsByPatient");
    }

    @RequestMapping(value = "clinic/getAllVitalRecordsByPatient", method = RequestMethod.POST)
    public ResponseEntity<String> getAllVitalRecordsByPatient(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/getAllVitalRecordsByPatient");
    }

    // ── STUDENT LOOKUP (for the ID-scan step) ───────────────────────────────
    // Reuses administration-pta's existing checkStudentByID — same lookup
    // AdministrationController already uses for gate/bus scanning, so an ID
    // card scan at the clinic resolves to a name the same way it does
    // everywhere else in the system.
    @RequestMapping(value = "clinic/checkStudentByID", method = RequestMethod.POST)
    public ResponseEntity<String> checkStudentByID(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/administration-pta/checkStudentByID");
    }

    // ── PHARMACY (medical products + stock movements) ───────────────────────
    @RequestMapping(value = "clinic/pharmacy/createProduct", method = RequestMethod.POST)
    public ResponseEntity<String> createProduct(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/pharmacy/createProduct");
    }

    @RequestMapping(value = "clinic/pharmacy/getProductsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> getProductsByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/pharmacy/getProductsByInstitution");
    }

    @RequestMapping(value = "clinic/pharmacy/getLowStockProducts", method = RequestMethod.POST)
    public ResponseEntity<String> getLowStockProducts(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/pharmacy/getLowStockProducts");
    }

    @RequestMapping(value = "clinic/pharmacy/deactivateProduct/{productId}", method = RequestMethod.POST)
    public ResponseEntity<String> deactivateProduct(@PathVariable Long productId) {
        return post(Map.of(), backendserve + "/api/clinic/pharmacy/deactivateProduct/" + productId);
    }

    @RequestMapping(value = "clinic/pharmacy/recordMovement", method = RequestMethod.POST)
    public ResponseEntity<String> recordMovement(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/pharmacy/recordMovement");
    }

    @RequestMapping(value = "clinic/pharmacy/getMovementsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> getMovementsByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/clinic/pharmacy/getMovementsByInstitution");
    }

    @RequestMapping(value = "clinic/pharmacy/getMovementsByProduct/{productId}", method = RequestMethod.POST)
    public ResponseEntity<String> getMovementsByProduct(@PathVariable Long productId) {
        return post(Map.of(), backendserve + "/api/clinic/pharmacy/getMovementsByProduct/" + productId);
    }
}

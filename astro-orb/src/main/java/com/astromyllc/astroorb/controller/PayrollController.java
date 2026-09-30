package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.subscription.RequiresPlan;
import com.astromyllc.astroorb.subscription.SubscriptionPlan;
import com.astromyllc.astroorb.utils.TenantResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Payroll: salary settings, staff salary profiles, monthly runs, approval,
 * payment and payslips.
 *
 * Multi-tenancy and audit: the school always comes from the signed-in user's
 * token (TenantResolver), and "created by / approved by / paid by" from the
 * login - anything the browser sends for those is overwritten. Staff always
 * come from HR: a profile can only be saved for someone in the school's HR
 * list, with name and designation copied from HR, and payroll only runs for
 * staff still listed there.
 */
@Controller
@ResponseBody
@Slf4j
@RequiresPlan(SubscriptionPlan.GROWTH)
public class PayrollController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gateway.host}")
    private String backendserve;

    // ── Settings ─────────────────────────────────────────────────────────────

    @PostMapping("salary-settings/get")
    public ResponseEntity<String> getSettings(@AuthenticationPrincipal OAuth2User principal) {
        return withSchool(principal, code ->
                post(Map.of("institutionCode", code), "/api/finance/salary-settings/get"));
    }

    @PostMapping("salary-settings/save")
    public ResponseEntity<String> saveSettings(@RequestBody Map<String, Object> body,
                                               @AuthenticationPrincipal OAuth2User principal) {
        return withSchool(principal, code -> {
            body.put("institutionCode", code);
            return post(body, "/api/finance/salary-settings/save");
        });
    }

    // ── Staff (from HR) and salary profiles ─────────────────────────────────

    /** The school's staff from HR, with the fields payroll and payslips use. */
    @PostMapping("payroll/staff")
    public ResponseEntity<?> getStaff(@AuthenticationPrincipal OAuth2User principal) {
        Optional<String> code = TenantResolver.institutionCode(principal);
        if (code.isEmpty()) return notLinkedToSchool();
        Optional<List<Map<String, Object>>> staff = hrStaff(code.get());
        return staff.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(this::hrUnavailable);
    }

    @PostMapping("payroll/profiles/get")
    public ResponseEntity<String> getProfiles(@AuthenticationPrincipal OAuth2User principal) {
        return withSchool(principal, code ->
                post(Map.of("institutionCode", code), "/api/finance/payroll/profiles/get"));
    }

    @PostMapping("payroll/profiles/save")
    public ResponseEntity<?> saveProfile(@RequestBody Map<String, Object> body,
                                         @AuthenticationPrincipal OAuth2User principal) {
        Optional<String> code = TenantResolver.institutionCode(principal);
        if (code.isEmpty()) return notLinkedToSchool();

        Optional<List<Map<String, Object>>> staff = hrStaff(code.get());
        if (staff.isEmpty()) return hrUnavailable();

        String staffId = String.valueOf(body.getOrDefault("staffId", "")).trim();
        Optional<Map<String, Object>> hrRecord = staff.get().stream()
                .filter(s -> staffId.equalsIgnoreCase(String.valueOf(s.get("staffId"))))
                .findFirst();
        if (hrRecord.isEmpty()) {
            return message(HttpStatus.BAD_REQUEST, "Choose a staff member from HR.");
        }

        body.put("institutionCode", code.get());
        body.put("staffId", hrRecord.get().get("staffId"));
        body.put("staffName", hrRecord.get().get("staffName"));
        body.put("designation", hrRecord.get().get("designation"));
        body.put("updatedBy", TenantResolver.userDisplayName(principal).orElse(null));
        return post(body, "/api/finance/payroll/profiles/save");
    }

    // ── Runs ─────────────────────────────────────────────────────────────────

    /** Creates this period's runs for every active profile of staff listed in HR. */
    @PostMapping("payroll/run")
    public ResponseEntity<?> runPayroll(@RequestBody Map<String, Object> body,
                                        @AuthenticationPrincipal OAuth2User principal) {
        Optional<String> code = TenantResolver.institutionCode(principal);
        if (code.isEmpty()) return notLinkedToSchool();

        Optional<List<Map<String, Object>>> staff = hrStaff(code.get());
        if (staff.isEmpty()) return hrUnavailable();

        Map<String, Object> request = new LinkedHashMap<>();
        request.put("institutionCode", code.get());
        request.put("academicYear", body.get("academicYear"));
        request.put("payPeriod", body.get("payPeriod"));
        request.put("createdBy", TenantResolver.userDisplayName(principal).orElse(null));
        request.put("staff", staff.get().stream()
                .map(s -> Map.of(
                        "staffId", String.valueOf(s.get("staffId")),
                        "staffName", Objects.toString(s.get("staffName"), ""),
                        "designation", Objects.toString(s.get("designation"), "")))
                .toList());
        return post(request, "/api/finance/payroll/run");
    }

    @PostMapping("salary/get-by-institution")
    public ResponseEntity<String> getRuns(@RequestBody Map<String, Object> body,
                                          @AuthenticationPrincipal OAuth2User principal) {
        return withSchool(principal, code -> {
            body.put("institutionCode", code);
            return post(body, "/api/finance/salary/get-by-institution");
        });
    }

    @PostMapping("salary/payslip")
    public ResponseEntity<String> getPayslip(@RequestBody Map<String, Object> body,
                                             @AuthenticationPrincipal OAuth2User principal) {
        return withSchool(principal, code -> {
            body.put("institutionCode", code);
            return post(body, "/api/finance/salary/payslip");
        });
    }

    // ── Approval and payment (Finance -> Salary Approvals) ───────────────────

    @PostMapping("payroll/approve")
    public ResponseEntity<String> approve(@RequestBody Map<String, Object> body,
                                          @AuthenticationPrincipal OAuth2User principal) {
        return action(body, principal, "/api/finance/payroll/approve");
    }

    @PostMapping("payroll/mark-paid")
    public ResponseEntity<String> markPaid(@RequestBody Map<String, Object> body,
                                           @AuthenticationPrincipal OAuth2User principal) {
        return action(body, principal, "/api/finance/payroll/mark-paid");
    }

    @PostMapping("payroll/delete-pending")
    public ResponseEntity<String> deletePending(@RequestBody Map<String, Object> body,
                                                @AuthenticationPrincipal OAuth2User principal) {
        return action(body, principal, "/api/finance/payroll/delete-pending");
    }

    private ResponseEntity<String> action(Map<String, Object> body, OAuth2User principal, String path) {
        return withSchool(principal, code -> {
            Map<String, Object> request = new HashMap<>();
            request.put("institutionCode", code);
            request.put("salaryIds", body.get("salaryIds"));
            request.put("externalReference", body.get("externalReference"));
            request.put("actor", TenantResolver.userDisplayName(principal).orElse(null));
            return post(request, path);
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private interface SchoolCall {
        ResponseEntity<String> call(String institutionCode);
    }

    private ResponseEntity<String> withSchool(OAuth2User principal, SchoolCall call) {
        Optional<String> code = TenantResolver.institutionCode(principal);
        if (code.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"message\":\"Your account isn't linked to a school. Contact your administrator.\"}");
        }
        return call.call(code.get());
    }

    /**
     * The school's staff from HR, trimmed to what payroll needs. Empty when HR
     * can't be reached - callers refuse to continue rather than guess.
     */
    private Optional<List<Map<String, Object>>> hrStaff(String institutionCode) {
        ResponseEntity<String> response = post(Map.of("val", institutionCode), "/api/hr/getStaffByCode");
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return Optional.empty();
        }
        try {
            JsonNode root = mapper.readTree(response.getBody());
            if (!root.isArray()) return Optional.empty();
            List<Map<String, Object>> staff = new ArrayList<>();
            for (JsonNode s : root) {
                String staffId = text(s, "staffCode");
                if (staffId == null) continue;
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("staffId", staffId);
                m.put("staffName", Stream.of(text(s, "firstNames"), text(s, "lastName"))
                        .filter(p -> p != null && !p.isBlank())
                        .collect(Collectors.joining(" ")));
                m.put("designation", text(s, "designation"));
                m.put("level", text(s, "level"));
                m.put("snnitNumber", text(s, "snnitNumber"));
                m.put("nationalID", text(s, "nationalID"));
                m.put("nationalIDType", text(s, "nationalIDType"));
                m.put("dateOfEmployment", text(s, "dateOfEmployment"));
                staff.add(m);
            }
            staff.sort((a, b) -> String.valueOf(a.get("staffName")).toLowerCase(Locale.ROOT)
                    .compareTo(String.valueOf(b.get("staffName")).toLowerCase(Locale.ROOT)));
            return Optional.of(staff);
        } catch (IOException e) {
            log.error("Payroll: couldn't read HR staff list: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) return null;
        // Dates may arrive as [2018, 3, 1] depending on how HR serialises them.
        if (value.isArray() && value.size() >= 3) {
            return String.format("%04d-%02d-%02d", value.get(0).asInt(), value.get(1).asInt(), value.get(2).asInt());
        }
        return value.asText();
    }

    private ResponseEntity<String> post(Object body, String path) {
        String url = backendserve + path;
        log.info("Payroll proxy → {}", url);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException e) {
            log.error("Payroll proxy error for {}: {}", url, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payroll proxy interrupted for {}", url);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("{\"message\":\"The payroll service couldn't be reached. Try again.\"}");
    }

    private ResponseEntity<?> notLinkedToSchool() {
        return message(HttpStatus.FORBIDDEN, "Your account isn't linked to a school. Contact your administrator.");
    }

    private ResponseEntity<?> hrUnavailable() {
        return message(HttpStatus.BAD_GATEWAY, "Couldn't load your staff list from HR. Try again.");
    }

    private static ResponseEntity<?> message(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}

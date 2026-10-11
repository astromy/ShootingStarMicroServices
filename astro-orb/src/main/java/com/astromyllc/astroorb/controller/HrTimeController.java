package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.subscription.RequiresPlan;
import com.astromyllc.astroorb.subscription.SubscriptionPlan;
import com.astromyllc.astroorb.utils.PermissionGuard;
import com.astromyllc.astroorb.utils.TenantResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holidays, working hours, overtime, leave and attendance.
 *
 * Same rules as PayrollController: the school always comes from the signed-in
 * user's token, never from the request body, and "approved by / updated by"
 * come from the login - whatever the browser sends for those is overwritten.
 *
 * The browser may send employeeId "me" (or nothing) to mean itself; HR resolves
 * that from actorStaffCode, which is the signed-in user's Keycloak username
 * (a staff member's username is their staff code).
 *
 * Permissions are enforced here too (see the Perm constants), using the same "groups" claim the menu
 * reads, so a direct call to an endpoint is refused for someone without the permission.
 *
 * Not exposed here on purpose: getApprovedOvertimeForPayroll,
 * lockOvertimeForPayroll and releaseOvertimeForPayroll. Finance calls those
 * on HR directly, so the browser can't mark overtime as paid.
 */
@Controller
@ResponseBody
public class HrTimeController {

    private static final Logger log = LoggerFactory.getLogger(HrTimeController.class);

    /** A permission from the Permissions screen, with how to describe it to someone who lacks it. */
    private record Perm(String code, String label, String action) {
    }

    // Enforced on the server as well as hidden in the menu. Reads the Leave screen itself needs
    // (holiday calendar, working week, a person's own leave) are deliberately not listed.
    private static final Perm LEAVE_APPROVALS = new Perm("Human_Resource leave_approvals", "Leave Approvals", "see or decide other people's leave");
    private static final Perm OVERTIME = new Perm("Human_Resource overtime", "Overtime", "work with overtime");
    private static final Perm HOLIDAYS = new Perm("setup holidays", "Holidays", "change the holiday calendar");
    private static final Perm WORK_HOURS = new Perm("setup working_hours", "Working Hours", "change working hours or overtime rates");
    private static final Perm ENTITLEMENTS = new Perm("setup leave_entitlements", "Leave Entitlements", "change leave entitlements");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gateway.host}")
    private String backendserve;

    // ── Setup: holidays and working hours ───────────────────────────────────

    @PostMapping("getHolidays")
    public ResponseEntity<String> getHolidays(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/getHolidays");
    }

    @PostMapping("seedGhanaHolidays")
    public ResponseEntity<String> seedGhanaHolidays(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(HOLIDAYS, body, principal, "/api/hr/seedGhanaHolidays");
    }

    /** Asks HolidaysMap for a fresh copy of the year (HR limits this to once a day), then adds anything the school is missing. */
    @PostMapping("refreshHolidaysFromApi")
    public ResponseEntity<String> refreshHolidaysFromApi(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(HOLIDAYS, body, principal, "/api/hr/refreshHolidaysFromApi");
    }

    /** Where the year's dates came from and when - shown above the holiday list. */
    @PostMapping("getHolidaySyncStatus")
    public ResponseEntity<String> getHolidaySyncStatus(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(HOLIDAYS, body, principal, "/api/hr/getHolidaySyncStatus");
    }

    @PostMapping("saveHoliday")
    public ResponseEntity<String> saveHoliday(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(HOLIDAYS, body, principal, "/api/hr/saveHoliday");
    }

    @PostMapping("deleteHoliday")
    public ResponseEntity<String> deleteHoliday(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(HOLIDAYS, body, principal, "/api/hr/deleteHoliday");
    }

    @PostMapping("getWorkHoursSettings")
    public ResponseEntity<String> getWorkHoursSettings(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/getWorkHoursSettings");
    }

    @PostMapping("saveWorkHoursSettings")
    public ResponseEntity<String> saveWorkHoursSettings(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(WORK_HOURS, body, principal, "/api/hr/saveWorkHoursSettings", "updatedBy");
    }

    // ── Setup: leave entitlements ───────────────────────────────────────────

    @PostMapping("getLeaveEntitlements")
    public ResponseEntity<String> getLeaveEntitlements(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(ENTITLEMENTS, body, principal, "/api/hr/getLeaveEntitlements");
    }

    @PostMapping("saveLeaveEntitlements")
    public ResponseEntity<String> saveLeaveEntitlements(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(ENTITLEMENTS, body, principal, "/api/hr/saveLeaveEntitlements", "updatedBy");
    }

    // ── Overtime (feeds payroll, so it follows payroll's plan) ──────────────

    @RequiresPlan(SubscriptionPlan.GROWTH)
    @PostMapping("detectOvertime")
    public ResponseEntity<String> detectOvertime(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(OVERTIME, body, principal, "/api/hr/detectOvertime");
    }

    @RequiresPlan(SubscriptionPlan.GROWTH)
    @PostMapping("getOvertime")
    public ResponseEntity<String> getOvertime(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(OVERTIME, body, principal, "/api/hr/getOvertime");
    }

    @RequiresPlan(SubscriptionPlan.GROWTH)
    @PostMapping("approveOvertime")
    public ResponseEntity<String> approveOvertime(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(OVERTIME, body, principal, "/api/hr/approveOvertime", "decidedBy");
    }

    @RequiresPlan(SubscriptionPlan.GROWTH)
    @PostMapping("rejectOvertime")
    public ResponseEntity<String> rejectOvertime(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(OVERTIME, body, principal, "/api/hr/rejectOvertime", "decidedBy");
    }

    @PostMapping("getManHours")
    public ResponseEntity<String> getManHours(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(OVERTIME, body, principal, "/api/hr/getManHours");
    }

    // ── Leave and attendance (the endpoints the Leave screen already calls) ─

    @PostMapping("applyLeave")
    public ResponseEntity<String> applyLeave(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/applyLeave");
    }

    @PostMapping("approveLeave")
    public ResponseEntity<String> approveLeave(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return guarded(LEAVE_APPROVALS, body, principal, "/api/hr/approveLeave", "actor");
    }

    @PostMapping("getLeaveRequests")
    public ResponseEntity<String> getLeaveRequests(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        // A blank employee means "everyone in the school" (the approval queue); "me" is the person's own list.
        Object who = body == null ? null : body.get("employeeId");
        boolean everyone = who == null || who.toString().isBlank();
        return everyone
                ? guarded(LEAVE_APPROVALS, body, principal, "/api/hr/getLeaveRequests")
                : forward(body, principal, "/api/hr/getLeaveRequests");
    }

    @PostMapping("getLeaveBalance")
    public ResponseEntity<String> getLeaveBalance(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/getLeaveBalance");
    }

    @PostMapping("checkInEmployee")
    public ResponseEntity<String> checkInEmployee(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/checkInEmployee");
    }

    @PostMapping("checkOutEmployee")
    public ResponseEntity<String> checkOutEmployee(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/checkOutEmployee");
    }

    @PostMapping("getEmployeeAttendance")
    public ResponseEntity<String> getEmployeeAttendance(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(body, principal, "/api/hr/getEmployeeAttendance");
    }

    // ── Plumbing ────────────────────────────────────────────────────────────

    /** Like forward(), but only for someone who holds the permission - checked here, not just hidden in the menu. */
    private ResponseEntity<String> guarded(Perm required, Map<String, Object> body, OAuth2User principal, String path, String... auditFields) {
        if (!PermissionGuard.has(principal, required.code())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\":\"You don't have permission to " + required.action()
                    + ". Ask an administrator to give you the " + required.label() + " permission.\"}");
        }
        return forward(body, principal, path, auditFields);
    }

    /**
     * Sends the body to HR with the school and the signed-in user filled in from the login.
     *
     * @param auditFields body keys that must hold the signed-in user's name (approved by, ...)
     */
    private ResponseEntity<String> forward(Map<String, Object> body, OAuth2User principal, String path, String... auditFields) {
        Optional<String> school = TenantResolver.institutionCode(principal);
        if (school.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"error\":\"Your account isn't linked to a school. Contact your administrator.\"}");
        }
        Map<String, Object> out = new HashMap<>(body == null ? Map.of() : body);
        out.put("institutionCode", school.get());
        out.put("actorStaffCode", staffCode(principal));
        String who = TenantResolver.userDisplayName(principal).orElse(null);
        for (String field : auditFields) {
            out.put(field, who);
        }
        return post(out, path);
    }

    /** A staff member's Keycloak username is their staff code. */
    private static String staffCode(OAuth2User principal) {
        Object username = principal.getAttribute("preferred_username");
        return username == null ? null : username.toString().trim();
    }

    private ResponseEntity<String> post(Object body, String path) {
        String url = backendserve + path;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException e) {
            log.error("HR time proxy error for {}: {}", url, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("HR time proxy interrupted for {}", url);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("{\"error\":\"The HR service couldn't be reached. Try again.\"}");
    }
}

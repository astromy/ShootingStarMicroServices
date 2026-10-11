package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.service.DashboardSummaryService;
import com.astromyllc.astroorb.utils.TenantResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Dashboard numbers for the signed-in user's school: a few hundred bytes instead of every
 * student, staff, bill and payment record.
 *
 * The school always comes from the login (like the other screens built on TenantResolver), never
 * from the request, so one school cannot ask for another school's numbers.
 */
@Controller
@ResponseBody
public class DashboardSummaryController {

    private final DashboardSummaryService summaries;

    public DashboardSummaryController(DashboardSummaryService summaries) {
        this.summaries = summaries;
    }

    /** Body (optional): {"parts": ["students", "staff", "finance"]} - ask only for what the dashboard shows. */
    @PostMapping("dashboard-summary")
    public ResponseEntity<String> dashboardSummary(@RequestBody(required = false) Map<String, Object> body,
                                                   @AuthenticationPrincipal OAuth2User principal) {
        Optional<String> school = TenantResolver.institutionCode(principal);
        if (school.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"Your account isn't linked to a school. Contact your administrator.\"}");
        }
        DashboardSummaryService.Result result = summaries.summary(school.get(), parts(body));
        HttpStatus status = result.anySucceeded() ? HttpStatus.OK : HttpStatus.BAD_GATEWAY;
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(result.json().toString());
    }

    private static List<String> parts(Map<String, Object> body) {
        List<String> parts = new ArrayList<>();
        if (body == null) return parts;
        Object requested = body.get("parts");
        if (requested instanceof List<?> list) {
            for (Object item : list) if (item != null) parts.add(item.toString());
        } else if (requested instanceof String csv) {
            for (String item : csv.split(",")) parts.add(item);
        }
        return parts;
    }
}

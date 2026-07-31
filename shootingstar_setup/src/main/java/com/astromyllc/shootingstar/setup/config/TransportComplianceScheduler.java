package com.astromyllc.shootingstar.setup.config;

import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.StaffContactResponse;
import com.astromyllc.shootingstar.setup.model.Bus;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import com.astromyllc.shootingstar.setup.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Runs once a day. For every institution, flags any bus whose insurance or
// roadworthy expiry is already past, or due within WARNING_WINDOW_DAYS, and
// notifies that institution's contact email plus every staff member whose
// designation is "Admin" — by email (existing MailUtil/ZeptoMail setup) and
// by push (via HR's registerPushToken/sendPushToStaff, since push tokens
// live there alongside Staff).
//
// Cross-service calls to HR go through the shared API gateway host, the
// same way InstitutionUtils.getPopulation() already calls administration-pta
// directly — not through astro-orb, which is mobile/web-client-facing only.
@Component
@RequiredArgsConstructor
@Slf4j
public class TransportComplianceScheduler {

    private static final int WARNING_WINDOW_DAYS = 14;

    private final WebClient.Builder webClientBuilder;
    private final MailUtil mailUtil;

    @Value("${gateway.host}")
    private String host;

    @Scheduled(cron = "0 0 7 * * ?")
    public void checkTransportCompliance() {
        InstitutionUtils.institutionGlobalList.forEach(this::checkInstitution);
    }

    private void checkInstitution(Institution institution) {
        List<Bus> buses = institution.getBusList();
        if (buses == null || buses.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate warningCutoff = today.plusDays(WARNING_WINDOW_DAYS);

        List<String> issues = buses.stream()
                .flatMap(bus -> describeIssues(bus, today, warningCutoff).stream())
                .collect(Collectors.toList());

        if (issues.isEmpty()) {
            return;
        }

        String subject = "Transport compliance alert - " + institution.getName();
        String body = "<p>The following buses need attention:</p><ul>"
                + issues.stream().map(i -> "<li>" + i + "</li>").collect(Collectors.joining())
                + "</ul>";

        notifyInstitution(institution, subject, body, String.join("; ", issues));
    }

    private List<String> describeIssues(Bus bus, LocalDate today, LocalDate warningCutoff) {
        List<String> issues = new java.util.ArrayList<>();
        addIfDue(issues, bus, "Insurance", bus.getInsuranceExpiryDate(), today, warningCutoff);
        addIfDue(issues, bus, "Roadworthy certificate", bus.getRoadworthyExpiryDate(), today, warningCutoff);
        return issues;
    }

    private void addIfDue(List<String> issues, Bus bus, String label, LocalDate expiryDate, LocalDate today, LocalDate warningCutoff) {
        if (expiryDate == null) {
            return;
        }
        if (expiryDate.isBefore(today)) {
            issues.add(bus.getName() + ": " + label + " expired on " + expiryDate);
        } else if (!expiryDate.isAfter(warningCutoff)) {
            issues.add(bus.getName() + ": " + label + " expires on " + expiryDate);
        }
    }

    private void notifyInstitution(Institution institution, String subject, String htmlBody, String pushBody) {
        try {
            List<StaffContactResponse> admins = fetchAdminStaff(institution.getBececode());

            mailUtil.sendTransactionalEmail(institution.getEmail(), subject, htmlBody, "ShootingStar Transport", true);
            admins.forEach(admin -> {
                if (admin.getStaffEmail() != null && !admin.getStaffEmail().isBlank()) {
                    mailUtil.sendTransactionalEmail(admin.getStaffEmail(), subject, htmlBody, "ShootingStar Transport", true);
                }
            });

            List<String> adminStaffCodes = admins.stream().map(StaffContactResponse::getStaffCode).collect(Collectors.toList());
            if (!adminStaffCodes.isEmpty()) {
                sendPushToAdmins(adminStaffCodes, subject, pushBody);
            }
        } catch (Exception e) {
            log.warn("Failed to notify institution {} of transport compliance issues: {}", institution.getBececode(), e.getMessage());
        }
    }

    private List<StaffContactResponse> fetchAdminStaff(String institutionCode) {
        return webClientBuilder.build()
                .post()
                .uri(host + "/api/hr/getAdminStaffByInstitution")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(SingleStringRequest.builder().val(institutionCode).build())
                .retrieve()
                .bodyToMono(StaffContactResponse[].class)
                .map(List::of)
                .onErrorReturn(List.of())
                .block();
    }

    private void sendPushToAdmins(List<String> staffCodes, String title, String body) {
        Map<String, Object> pushRequest = Map.of("staffCodes", staffCodes, "title", title, "body", body);
        webClientBuilder.build()
                .post()
                .uri(host + "/api/hr/sendPushToStaff")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(pushRequest)
                .retrieve()
                .toBodilessEntity()
                .onErrorComplete()
                .block();
    }
}
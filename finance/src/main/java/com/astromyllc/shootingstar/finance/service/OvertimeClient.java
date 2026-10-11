package com.astromyllc.shootingstar.finance.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Asks HR - through the gateway, like every other service - for the overtime
 * that has been approved but not yet paid, and tells it which payroll runs have
 * taken it. Any failure becomes an IllegalStateException whose message the
 * payroll page shows as-is; payroll never carries on without the overtime.
 */
@Component
public class OvertimeClient {

    /** date is kept as text: only the grouping and amounts matter here. */
    public record Item(String id, String staffCode, String date, String category, int minutes) {
    }

    public record Batch(boolean enabled, double monthlyHoursDivisor, double multiplierAfterHours,
                        double multiplierWeekend, double multiplierHoliday, List<Item> items) {
    }

    public record Allocation(String payrollRef, List<String> recordIds) {
    }

    public record Count(int count) {
    }

    private static final Duration TIMEOUT = Duration.ofSeconds(20);
    private static final Pattern ERROR_FIELD = Pattern.compile("\"error\"\\s*:\\s*\"([^\"]*)\"");

    private final WebClient webClient;

    public OvertimeClient(WebClient.Builder builder, @Value("${gateway.host}") String gatewayHost) {
        this.webClient = builder.baseUrl(gatewayHost).build();
    }

    public Batch fetch(String institutionCode, LocalDate toDate) {
        return call("/api/hr/getApprovedOvertimeForPayroll",
                Map.of("institutionCode", institutionCode, "toDate", toDate.toString()), Batch.class);
    }

    public void lock(String institutionCode, List<Allocation> allocations) {
        call("/api/hr/lockOvertimeForPayroll",
                Map.of("institutionCode", institutionCode, "allocations", allocations), Count.class);
    }

    public void release(String institutionCode, List<String> payrollRefs) {
        call("/api/hr/releaseOvertimeForPayroll",
                Map.of("institutionCode", institutionCode, "payrollRefs", payrollRefs), Count.class);
    }

    private <T> T call(String path, Object body, Class<T> type) {
        try {
            T result = webClient.post().uri(path).contentType(MediaType.APPLICATION_JSON).bodyValue(body)
                    .retrieve().bodyToMono(type).block(TIMEOUT);
            if (result == null) {
                throw new IllegalStateException("HR returned nothing when asked for overtime. Try again.");
            }
            return result;
        } catch (WebClientResponseException e) {
            Matcher m = ERROR_FIELD.matcher(e.getResponseBodyAsString());
            throw new IllegalStateException(m.find() ? m.group(1)
                    : "HR couldn't handle the overtime request (error " + e.getStatusCode().value() + "). Try again.");
        } catch (IllegalStateException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IllegalStateException("HR couldn't be reached to collect approved overtime. Try again in a moment.");
        }
    }
}

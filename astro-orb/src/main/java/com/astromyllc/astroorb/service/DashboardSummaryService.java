package com.astromyllc.astroorb.service;

import com.astromyllc.astroorb.utils.BackendClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The numbers behind the dashboards, in one small response.
 *
 * Asks the students, staff and finance services (in parallel) for counts they work out from
 * their in-memory lists, and returns them together. Each school's numbers are kept for a few
 * seconds, so a room full of people opening the dashboard at the start of the day makes one set of
 * calls, not one per person. If a service is briefly down, its last numbers are shown (up to
 * ten minutes old) instead of an empty dashboard.
 */
@Service
public class DashboardSummaryService {

    private static final Logger log = LoggerFactory.getLogger(DashboardSummaryService.class);

    /** The parts a dashboard can ask for, and where each one is worked out. */
    static final Map<String, String> PARTS = Map.of(
            "students", "/api/administration-pta/dashboard-summary",
            "staff", "/api/hr/staff-summary",
            "finance", "/api/finance/dashboard-summary");

    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(20);
    private static final long STALE_LIMIT_MILLIS = Duration.ofMinutes(10).toMillis();
    private static final int MAX_CACHED = 2000;

    private record Cached(long fetchedAt, JsonNode value) {
    }

    private static final AtomicInteger THREADS = new AtomicInteger();
    private static final ExecutorService POOL = Executors.newFixedThreadPool(8, task -> {
        Thread t = new Thread(task, "dashboard-summary-" + THREADS.incrementAndGet());
        t.setDaemon(true);
        return t;
    });

    private final BackendClient backend;
    private final long ttlMillis;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Cached> cache = new ConcurrentHashMap<>();

    public DashboardSummaryService(BackendClient backend,
                                   @Value("${dashboard.summary.ttl-seconds:30}") long ttlSeconds) {
        this.backend = backend;
        this.ttlMillis = Math.max(0, ttlSeconds) * 1000;
    }

    /** What a dashboard gets back: the parts that worked, and a message for each that did not. */
    public record Result(ObjectNode json, boolean anySucceeded) {
    }

    /**
     * @param school the signed-in user's school
     * @param parts  any of students, staff, finance (unknown names are ignored); empty means all
     */
    public Result summary(String school, List<String> parts) {
        List<String> wanted = parts == null || parts.isEmpty() ? List.of("students", "staff", "finance")
                : parts.stream().map(p -> p == null ? "" : p.trim().toLowerCase()).filter(PARTS::containsKey).distinct().toList();

        Map<String, CompletableFuture<JsonNode>> pending = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();
        for (String part : wanted) {
            pending.put(part, CompletableFuture.supplyAsync(() -> load(school, part), POOL));
        }

        ObjectNode out = mapper.createObjectNode();
        boolean anyOk = false;
        for (Map.Entry<String, CompletableFuture<JsonNode>> e : pending.entrySet()) {
            try {
                out.set(e.getKey(), e.getValue().join());
                anyOk = true;
            } catch (RuntimeException failure) {
                Throwable cause = failure.getCause() == null ? failure : failure.getCause();
                log.warn("Dashboard {} summary for {} failed: {}", e.getKey(), school, cause.getMessage());
                errors.put(e.getKey(), "The " + e.getKey() + " numbers could not be loaded right now.");
            }
        }
        if (!errors.isEmpty()) out.set("errors", mapper.valueToTree(errors));
        return new Result(out, anyOk || wanted.isEmpty());
    }

    private JsonNode load(String school, String part) {
        String key = school.toLowerCase() + "/" + part;
        long now = System.currentTimeMillis();
        Cached cached = cache.get(key);
        if (cached != null && now - cached.fetchedAt() < ttlMillis) return cached.value();

        try {
            BackendClient.Reply reply = backend.post(PARTS.get(part), Map.of("val", school), CALL_TIMEOUT);
            if (reply.status() / 100 != 2) {
                throw new IllegalStateException("HTTP " + reply.status());
            }
            JsonNode value = mapper.readTree(reply.body());
            if (cache.size() >= MAX_CACHED) cache.clear();
            cache.put(key, new Cached(now, value));
            return value;
        } catch (BackendClient.Unavailable | com.fasterxml.jackson.core.JsonProcessingException | IllegalStateException e) {
            if (cached != null && now - cached.fetchedAt() < STALE_LIMIT_MILLIS) {
                log.warn("Using the earlier {} numbers for {} ({})", part, school, e.getMessage());
                return cached.value();
            }
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}

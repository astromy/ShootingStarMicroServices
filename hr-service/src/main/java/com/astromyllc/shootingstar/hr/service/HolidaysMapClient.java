package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.exception.HolidayApiException;
import com.astromyllc.shootingstar.hr.exception.HolidayApiException.Kind;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ghana's public holidays from HolidaysMap (GET /api/v1/holidays).
 *
 * Plans matter, so two ways of asking are supported:
 *   fetchRange - ONE request for the whole year (start and end). Needs Premium.
 *   fetchDaily - one request per day, 365 a year. Works on any plan, but the free
 *                plan only allows 100 requests a month, so it is off unless
 *                holidays.api.daily-fallback=true.
 *
 * Configuration (set the key as the environment variable HOLIDAYS_API_KEY or in
 * .env; it must never be written into source or committed):
 *   holidays.api.key             the API key
 *   holidays.api.base-url        default https://holidaysmap.com
 *   holidays.api.country-code    default GH
 *   holidays.api.daily-fallback  default false
 */
@Component
public class HolidaysMapClient implements HolidayProvider {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);
    /** A year is 366 days at most; this is a hard stop so a bug can never burn through a quota. */
    private static final int MAX_DAILY_REQUESTS = 400;

    private final String baseUrl;
    private final String key;
    private final String countryCode;
    private final boolean dailyFallback;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public HolidaysMapClient(@Value("${holidays.api.base-url:https://holidaysmap.com}") String baseUrl,
                             @Value("${holidays.api.key:}") String key,
                             @Value("${holidays.api.country-code:GH}") String countryCode,
                             @Value("${holidays.api.daily-fallback:false}") boolean dailyFallback) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.key = key == null ? "" : key.trim();
        this.countryCode = countryCode;
        this.dailyFallback = dailyFallback;
    }

    @Override
    public String id() {
        return "HOLIDAYSMAP";
    }

    @Override
    public String label() {
        return "HolidaysMap";
    }

    @Override
    public boolean isConfigured() {
        return !key.isEmpty();
    }

    @Override
    public Fetched fetchYear(int year) {
        return fetchRange(year);
    }

    @Override
    public boolean canFetchDaily() {
        return dailyFallback;
    }

    public boolean dailyFallbackEnabled() {
        return dailyFallback;
    }

    /** The whole year in one request. */
    public Fetched fetchRange(int year) {
        requireKey();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("start", year + "-01-01");
        params.put("end", year + "-12-31");
        List<ApiHoliday> all = new ArrayList<>(parse(call(params, true), year));
        List<ApiHoliday> unique = dedupe(all);
        if (unique.isEmpty()) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "HolidaysMap returned no holidays for " + year + ".");
        }
        return new Fetched(unique, "RANGE", 1);
    }

    /** One request per day of the year. */
    @Override
    public Fetched fetchDaily(int year) {
        requireKey();
        List<ApiHoliday> all = new ArrayList<>();
        int requests = 0;
        for (LocalDate d = LocalDate.of(year, 1, 1); d.getYear() == year; d = d.plusDays(1)) {
            if (requests >= MAX_DAILY_REQUESTS) break;
            Map<String, String> params = new LinkedHashMap<>();
            params.put("date", d.toString());
            all.addAll(parse(call(params, false), year));
            requests++;
        }
        List<ApiHoliday> unique = dedupe(all);
        if (unique.isEmpty()) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "HolidaysMap returned no holidays for " + year + ".");
        }
        return new Fetched(unique, "DAILY", requests);
    }

    // ── internals ───────────────────────────────────────────────────────────

    private void requireKey() {
        if (!isConfigured()) {
            throw new HolidayApiException(Kind.NOT_CONFIGURED, "No HolidaysMap API key is set on the server.");
        }
    }

    private String call(Map<String, String> params, boolean isRange) {
        StringBuilder url = new StringBuilder(baseUrl).append("/api/v1/holidays?key=").append(enc(key))
                .append("&code=").append(enc(countryCode))
                .append("&format=YYYY-MM-DD&output=json");
        params.forEach((k, v) -> url.append('&').append(k).append('=').append(enc(v)));

        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url.toString())).timeout(TIMEOUT).GET().build();
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HolidayApiException(Kind.UNAVAILABLE, "The request to HolidaysMap was interrupted.");
        } catch (IOException | RuntimeException e) {
            // Deliberately not e.getMessage(): it can contain the request URL, and so the key.
            throw new HolidayApiException(Kind.UNAVAILABLE, "HolidaysMap could not be reached.");
        }

        int status = response.statusCode();
        if (status == 200) return response.body();
        if (status == 401) {
            throw new HolidayApiException(Kind.KEY_REJECTED,
                    "HolidaysMap did not accept the API key (401). Check the key, and that the HolidaysMap account is activated.");
        }
        if (status == 429) {
            throw new HolidayApiException(Kind.QUOTA, "The HolidaysMap request allowance for this month has been used up (429).");
        }
        if (status >= 500) {
            throw new HolidayApiException(Kind.UNAVAILABLE, "HolidaysMap had a problem on its side (" + status + "). Try again later.");
        }
        if (isRange) {
            throw new HolidayApiException(Kind.RANGE_UNAVAILABLE,
                    "HolidaysMap refused the year-at-once request (" + status + "); date ranges need its Premium plan.");
        }
        throw new HolidayApiException(Kind.UNAVAILABLE, "HolidaysMap refused the request (" + status + ").");
    }

    private List<ApiHoliday> parse(String body, int year) {
        JsonNode root;
        try {
            root = mapper.readTree(body);
        } catch (Exception e) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "HolidaysMap sent something that isn't JSON.");
        }
        JsonNode list = root == null ? null : root.path("holidays");
        if (list == null || !list.isArray()) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "HolidaysMap's answer had no list of holidays.");
        }
        List<ApiHoliday> out = new ArrayList<>();
        for (JsonNode h : list) {
            // Only real public holidays: commemorative days are marked not-public.
            if (!h.path("is_public").asBoolean(true)) continue;
            String name = h.path("name").asText("").trim();
            if (name.isEmpty()) continue;
            LocalDate date;
            try {
                date = LocalDate.parse(h.path("date").asText(""));
            } catch (DateTimeParseException e) {
                continue;
            }
            if (date.getYear() != year) continue;
            out.add(new ApiHoliday(name, date, date));
        }
        return out;
    }

    private static List<ApiHoliday> dedupe(List<ApiHoliday> all) {
        Map<String, ApiHoliday> seen = new LinkedHashMap<>();
        for (ApiHoliday h : all) seen.putIfAbsent(h.statutoryDate() + "|" + h.name().toLowerCase(), h);
        List<ApiHoliday> out = new ArrayList<>(seen.values());
        out.sort((a, b) -> a.statutoryDate().compareTo(b.statutoryDate()));
        return out;
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

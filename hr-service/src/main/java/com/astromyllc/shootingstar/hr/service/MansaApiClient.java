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
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Ghana's public holidays from Mansa API:
 *   GET {base}/api/v1/location/countries/GH/holidays/{year}   (Authorization: Bearer mansa_live_sk_...)
 *
 * One request returns the whole year, and the free tier allows 100 requests a day, so fetching a
 * year costs 1 request - which is why this provider is tried first.
 *
 * Mansa publishes a "substitute day" next to a holiday that falls on a weekend (e.g. "Eid al-Fitr" on
 * a Sunday plus "Eid al-Fitr (substitute day)" on the Monday). Those are merged into ONE holiday: the
 * law's date is the original, and staff are off on the substitute.
 *
 * The documentation does not spell out the fields of each holiday, so parsing accepts the usual
 * variations (name/title, date as yyyy-MM-dd, is_public, a substitute flag or "(substitute day)" in the
 * name) and ignores anything it can't read rather than guessing.
 *
 * Configuration (set the key as the environment variable HOLIDAYS_MANSA_KEY; never commit it):
 *   holidays.mansa.key, holidays.mansa.base-url (default https://mansaapi.com), holidays.mansa.country-code (default GH)
 */
@Component
public class MansaApiClient implements HolidayProvider {

    private static final Duration TIMEOUT = Duration.ofSeconds(15);
    private static final Pattern SUBSTITUTE = Pattern.compile("(?i)\\s*[\\(\\[]\\s*substitute( day)?\\s*[\\)\\]]\\s*$");

    private final String baseUrl;
    private final String key;
    private final String countryCode;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper mapper = new ObjectMapper();

    public MansaApiClient(@Value("${holidays.mansa.base-url:https://mansaapi.com}") String baseUrl,
                          @Value("${holidays.mansa.key:}") String key,
                          @Value("${holidays.mansa.country-code:GH}") String countryCode) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.key = key == null ? "" : key.trim();
        this.countryCode = countryCode;
    }

    @Override
    public String id() {
        return "MANSA";
    }

    @Override
    public String label() {
        return "Mansa API";
    }

    @Override
    public boolean isConfigured() {
        return !key.isEmpty();
    }

    @Override
    public Fetched fetchYear(int year) {
        if (!isConfigured()) {
            throw new HolidayApiException(Kind.NOT_CONFIGURED, "No Mansa API key is set on the server.");
        }
        String url = baseUrl + "/api/v1/location/countries/" + enc(countryCode) + "/holidays/" + year;
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(TIMEOUT)
                    .header("Authorization", "Bearer " + key).header("Accept", "application/json").GET().build();
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HolidayApiException(Kind.UNAVAILABLE, "The request to Mansa API was interrupted.");
        } catch (IOException | RuntimeException e) {
            // Deliberately not e.getMessage(): keep every trace of the request (and the key) out of the message.
            throw new HolidayApiException(Kind.UNAVAILABLE, "Mansa API could not be reached.");
        }

        int status = response.statusCode();
        if (status == 401) {
            String code = errorCode(response.body());
            throw new HolidayApiException(Kind.KEY_REJECTED, "Mansa API did not accept the API key (401"
                    + (code.isEmpty() ? "" : ", " + code) + "). Check the key.");
        }
        if (status == 429) {
            throw new HolidayApiException(Kind.QUOTA, "Mansa API's daily request limit has been reached (429). It resets each day.");
        }
        if (status >= 500) {
            throw new HolidayApiException(Kind.UNAVAILABLE, "Mansa API had a problem on its side (" + status + "). Try again later.");
        }
        if (status != 200) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "Mansa API has no holidays for " + year + " (" + status + ").");
        }

        List<ApiHoliday> holidays = merge(parse(response.body()), year);
        if (holidays.isEmpty()) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "Mansa API returned no holidays for " + year + ".");
        }
        return new Fetched(holidays, "YEAR", 1);
    }

    // ── parsing ─────────────────────────────────────────────────────────────

    /** One row as Mansa sent it, before substitutes are merged. */
    private record Raw(String name, LocalDate date, boolean substitute) {
    }

    private List<Raw> parse(String body) {
        JsonNode root;
        try {
            root = mapper.readTree(body);
        } catch (Exception e) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "Mansa API sent something that isn't JSON.");
        }
        JsonNode list = findList(root);
        if (list == null) {
            throw new HolidayApiException(Kind.BAD_RESPONSE, "Mansa API's answer had no list of holidays.");
        }
        List<Raw> out = new ArrayList<>();
        for (JsonNode h : list) {
            if (!h.path("is_public").asBoolean(true) || !h.path("public").asBoolean(true)) continue;
            String name = firstText(h, "name", "title", "holiday", "local_name", "localName");
            if (name.isEmpty()) continue;
            LocalDate date;
            try {
                String raw = firstText(h, "date", "holiday_date", "observed");
                date = LocalDate.parse(raw.length() > 10 ? raw.substring(0, 10) : raw);
            } catch (DateTimeParseException e) {
                continue;
            }
            boolean substitute = h.path("is_substitute").asBoolean(false) || h.path("substitute").asBoolean(false)
                    || firstText(h, "type", "kind").toLowerCase(Locale.ROOT).contains("substitute")
                    || SUBSTITUTE.matcher(name).find();
            out.add(new Raw(SUBSTITUTE.matcher(name).replaceAll("").trim(), date, substitute));
        }
        return out;
    }

    private static JsonNode findList(JsonNode root) {
        if (root == null) return null;
        JsonNode data = root.path("data");
        if (data.isArray()) return data;
        if (data.path("holidays").isArray()) return data.path("holidays");
        if (root.path("holidays").isArray()) return root.path("holidays");
        return null;
    }

    private static String firstText(JsonNode node, String... fields) {
        for (String f : fields) {
            String v = node.path(f).asText("").trim();
            if (!v.isEmpty()) return v;
        }
        return "";
    }

    /** Folds each substitute day into its holiday: the law keeps the original date, staff are off on the substitute. */
    private static List<ApiHoliday> merge(List<Raw> raws, int year) {
        Map<String, ApiHoliday> byName = new LinkedHashMap<>();
        for (Raw r : raws) {
            if (r.substitute()) continue;
            if (r.date().getYear() != year) continue;
            byName.putIfAbsent(nameKey(r.name()) + "|" + r.date(), new ApiHoliday(r.name(), r.date(), r.date()));
        }
        for (Raw r : raws) {
            if (!r.substitute()) continue;
            ApiHoliday base = null;
            String baseKey = null;
            for (Map.Entry<String, ApiHoliday> e : byName.entrySet()) {
                // the original is the same-named holiday just before the substitute
                if (nameKey(e.getValue().name()).equals(nameKey(r.name())) && !e.getValue().statutoryDate().isAfter(r.date())) {
                    base = e.getValue();
                    baseKey = e.getKey();
                }
            }
            if (base != null) {
                byName.put(baseKey, new ApiHoliday(base.name(), base.statutoryDate(), r.date()));
            } else if (r.date().getYear() == year) {
                // a substitute with no original in this year (the original fell in the previous December): keep it
                byName.putIfAbsent(nameKey(r.name()) + "|" + r.date(), new ApiHoliday(r.name(), r.date(), r.date()));
            }
        }
        List<ApiHoliday> out = new ArrayList<>(byName.values());
        out.sort((a, b) -> a.statutoryDate().compareTo(b.statutoryDate()));
        return out;
    }

    private static String nameKey(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private String errorCode(String body) {
        try {
            return mapper.readTree(body).path("error").path("code").asText("");
        } catch (Exception e) {
            return "";
        }
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

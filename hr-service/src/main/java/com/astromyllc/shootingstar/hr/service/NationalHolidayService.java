package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.calc.GhanaHolidayRules;
import com.astromyllc.shootingstar.hr.exception.HolidayApiException;
import com.astromyllc.shootingstar.hr.model.HolidaySync;
import com.astromyllc.shootingstar.hr.model.NationalHoliday;
import com.astromyllc.shootingstar.hr.repository.HolidaySyncRepository;
import com.astromyllc.shootingstar.hr.repository.NationalHolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;

/**
 * Ghana's holiday dates for a year, fetched ONCE and kept.
 *
 * This is the national list, shared by every school: fetched once for the whole platform, then each
 * school gets its own editable copy (HolidayService). Providers (Mansa API, HolidaysMap) are tried in
 * the order they are registered, and the first good answer is kept. If none is set up or all fail, the
 * built-in rules are used - those are never stored as if they were fetched, so a later attempt can
 * still get the real thing.
 *
 * Protecting the providers' allowances:
 *   - a year already fetched is never fetched again unless an admin asks;
 *   - an admin refresh is allowed once a day per year;
 *   - after a failure nothing is retried automatically for an hour.
 */
@Service
public class NationalHolidayService {

    private static final Logger log = LoggerFactory.getLogger(NationalHolidayService.class);

    public static final String SOURCE_RULES = "RULES";

    static final Duration FAILURE_BACKOFF = Duration.ofHours(1);
    static final Duration REFRESH_COOLDOWN = Duration.ofHours(24);

    /** @param statutoryDate what the law names; observedDate when staff are off */
    public record Day(String name, LocalDate statutoryDate, LocalDate observedDate) {
    }

    /**
     * @param source     the provider id (MANSA, HOLIDAYSMAP) or RULES
     * @param provider   the provider's name for people, or null for RULES
     * @param note       what happened, for the admin; null when all is well
     * @param background true while a day-by-day fetch is still running
     */
    public record Outcome(String source, String provider, List<Day> days, String note, boolean background) {
        public boolean fromApi() {
            return !SOURCE_RULES.equals(source);
        }
    }

    public record Status(boolean apiConfigured, List<String> providers, boolean dailyFallbackEnabled, boolean cached,
                         int count, String provider, Instant fetchedAt, String mode, boolean lastAttemptFailed,
                         String message, boolean inProgress) {
    }

    private final List<HolidayProvider> providers;
    private final NationalHolidayRepository nationalRepository;
    private final HolidaySyncRepository syncRepository;

    private final Object lock = new Object();
    private final Map<Integer, Boolean> running = new ConcurrentHashMap<>();
    private final ExecutorService background = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "holiday-daily-fetch");
        t.setDaemon(true);
        return t;
    });
    private volatile IntConsumer onLoaded = year -> {
    };

    /** providers: in the order to try them (Spring injects every HolidayProvider bean). */
    public NationalHolidayService(List<HolidayProvider> providers, NationalHolidayRepository nationalRepository,
                                  HolidaySyncRepository syncRepository) {
        this.providers = providers;
        this.nationalRepository = nationalRepository;
        this.syncRepository = syncRepository;
    }

    /** Called when a year has finished loading in the background, so schools can be filled in. */
    public void setOnLoaded(IntConsumer onLoaded) {
        this.onLoaded = onLoaded;
    }

    /** @param force an admin asked for a fresh copy */
    public Outcome load(int year, boolean force) {
        synchronized (lock) {
            List<NationalHoliday> cached = nationalRepository.findByYear(year);
            HolidaySync last = lastSync(year);
            Instant now = Instant.now();

            if (!cached.isEmpty() && !force) return fromCache(cached, null);

            if (!cached.isEmpty() && last != null && last.isOk() && last.getFetchedAt() != null
                    && last.getFetchedAt().isAfter(now.minus(REFRESH_COOLDOWN))) {
                return fromCache(cached, "Already fetched less than a day ago, so it was not asked again.");
            }
            if (running.containsKey(year)) {
                return fallback(year, cached, "A day-by-day fetch is still running. Check back in a few minutes", true);
            }
            List<HolidayProvider> configured = new ArrayList<>();
            for (HolidayProvider p : providers) if (p.isConfigured()) configured.add(p);
            if (configured.isEmpty()) {
                return fallback(year, cached, "No holiday service is connected (no API key is set on the server)", false);
            }
            if (!force && last != null && !last.isOk() && last.getFetchedAt() != null
                    && last.getFetchedAt().isAfter(now.minus(FAILURE_BACKOFF))) {
                return fallback(year, cached, "The holiday services aren't being asked again yet because the last attempt failed: "
                        + last.getMessage(), false);
            }

            List<String> failures = new ArrayList<>();
            for (HolidayProvider p : configured) {
                try {
                    store(year, p, p.fetchYear(year));
                    Outcome ok = fromCache(nationalRepository.findByYear(year), null);
                    if (failures.isEmpty()) return ok;
                    // An earlier service failed but a later one worked: say so, so a broken key doesn't go unnoticed.
                    return new Outcome(ok.source(), ok.provider(), ok.days(),
                            String.join(" ", failures) + " " + p.label() + " was used instead.", false);
                } catch (HolidayApiException e) {
                    if (e.getKind() == HolidayApiException.Kind.RANGE_UNAVAILABLE && p.canFetchDaily()) {
                        startDaily(year, p);
                        return fallback(year, cached, p.label() + " can't fetch a whole year at once on this plan, so the dates are being "
                                + "fetched one day at a time in the background (about 365 requests)", true);
                    }
                    failures.add(p.label() + ": " + (e.getKind() == HolidayApiException.Kind.RANGE_UNAVAILABLE
                            ? e.getMessage() + " To use single-date requests instead (about 365 per year), set holidays.api.daily-fallback=true."
                            : e.getMessage()));
                }
            }
            String reason = String.join(" ", failures);
            recordFailure(year, reason);
            return fallback(year, cached, reason, false);
        }
    }

    public Status status(int year) {
        List<NationalHoliday> cached = nationalRepository.findByYear(year);
        HolidaySync last = lastSync(year);
        List<String> configured = new ArrayList<>();
        for (HolidayProvider p : providers) if (p.isConfigured()) configured.add(p.label());
        boolean failed = last != null && !last.isOk();
        boolean daily = false;
        for (HolidayProvider p : providers) daily = daily || (p.isConfigured() && p.canFetchDaily());
        return new Status(!configured.isEmpty(), configured, daily, !cached.isEmpty(), cached.size(),
                cached.isEmpty() ? null : labelOf(cached.get(0).getProvider()),
                last == null ? null : last.getFetchedAt(), last == null ? null : last.getMode(), failed,
                failed ? last.getMessage() : null, running.containsKey(year));
    }

    // ── internals ───────────────────────────────────────────────────────────

    private void startDaily(int year, HolidayProvider provider) {
        running.put(year, Boolean.TRUE);
        background.submit(() -> {
            try {
                HolidayProvider.Fetched fetched = provider.fetchDaily(year);
                synchronized (lock) {
                    store(year, provider, fetched);
                }
                onLoaded.accept(year);
            } catch (HolidayApiException e) {
                synchronized (lock) {
                    recordFailure(year, provider.label() + ": " + e.getMessage());
                }
            } catch (RuntimeException e) {
                log.warn("Day-by-day holiday fetch for {} failed: {}", year, e.getMessage());
                synchronized (lock) {
                    recordFailure(year, "The day-by-day fetch stopped unexpectedly.");
                }
            } finally {
                running.remove(year);
            }
        });
    }

    private void store(int year, HolidayProvider provider, HolidayProvider.Fetched fetched) {
        Instant now = Instant.now();
        nationalRepository.deleteAll(nationalRepository.findByYear(year));
        List<NationalHoliday> rows = new ArrayList<>();
        for (HolidayProvider.ApiHoliday h : fetched.holidays()) {
            NationalHoliday n = new NationalHoliday();
            n.setYear(year);
            n.setName(h.name());
            n.setDate(h.statutoryDate().toString());
            n.setObservedDate(h.observedDate().toString());
            n.setProvider(provider.id());
            n.setFetchedAt(now);
            rows.add(n);
        }
        nationalRepository.saveAll(rows);

        HolidaySync sync = lastSync(year);
        if (sync == null) sync = new HolidaySync();
        sync.setYear(year);
        sync.setOk(true);
        sync.setFetchedAt(now);
        sync.setCount(rows.size());
        sync.setProvider(provider.id());
        sync.setMode(fetched.mode());
        sync.setRequests(fetched.requests());
        sync.setMessage(null);
        syncRepository.save(sync);
        log.info("Fetched {} Ghana holidays for {} from {} ({} request(s), {})",
                rows.size(), year, provider.label(), fetched.requests(), fetched.mode());
    }

    private void recordFailure(int year, String message) {
        HolidaySync sync = lastSync(year);
        if (sync == null) {
            sync = new HolidaySync();
            sync.setYear(year);
        }
        // A failed refresh must not erase what the last good fetch kept: count, provider and mode stay.
        sync.setOk(false);
        sync.setFetchedAt(Instant.now());
        sync.setMessage(message);
        syncRepository.save(sync);
        log.warn("Holiday fetch for {} failed: {}", year, message);
    }

    private HolidaySync lastSync(int year) {
        List<HolidaySync> all = syncRepository.findByYear(year);
        return all.isEmpty() ? null : all.get(0);
    }

    private String labelOf(String providerId) {
        for (HolidayProvider p : providers) if (p.id().equals(providerId)) return p.label();
        return providerId;
    }

    private Outcome fromCache(List<NationalHoliday> cached, String note) {
        List<Day> days = new ArrayList<>();
        for (NationalHoliday n : cached) {
            LocalDate statutory = LocalDate.parse(n.getDate());
            LocalDate observed = n.getObservedDate() == null ? statutory : LocalDate.parse(n.getObservedDate());
            days.add(new Day(n.getName(), statutory, observed));
        }
        days.sort((a, b) -> a.observedDate().compareTo(b.observedDate()));
        String provider = cached.get(0).getProvider() == null ? "API" : cached.get(0).getProvider();
        return new Outcome(provider, labelOf(provider), days, note, false);
    }

    /**
     * When the services can't be used right now: keep serving what an earlier fetch stored, because a failed
     * refresh must never replace good dates with guesses. Only with nothing stored are the built-in rules used.
     */
    private Outcome fallback(int year, List<NationalHoliday> cached, String reason, boolean background) {
        if (!cached.isEmpty()) {
            Outcome kept = fromCache(cached, reason + ". The dates fetched earlier were kept.");
            return new Outcome(kept.source(), kept.provider(), kept.days(), kept.note(), background);
        }
        List<Day> days = new ArrayList<>();
        for (GhanaHolidayRules.Draft d : GhanaHolidayRules.suggestedHolidays(year)) {
            days.add(new Day(d.name(), d.statutoryDate(), d.observedDate()));
        }
        return new Outcome(SOURCE_RULES, null, days, reason + ", so the built-in rules were used.", background);
    }
}

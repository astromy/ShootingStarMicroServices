package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.model.PublicHoliday;
import com.astromyllc.shootingstar.hr.model.WorkHoursSettings;
import com.astromyllc.shootingstar.hr.repository.PublicHolidayRepository;
import com.astromyllc.shootingstar.hr.repository.WorkHoursSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Gets the year's holidays once, at the start of the year, and uses them all year.
 *
 * At 01:15 on 1 January (Ghana time) - and once at start-up, in case the service was
 * down on 1 January - it makes sure this year's national list has been fetched from
 * a holiday service (Mansa API, else HolidaysMap), then fills in every school that already uses the calendar and has
 * nothing for the year yet. Schools can still change anything afterwards.
 *
 * It only fills schools in when the dates really came from a holiday service. If none
 * is available it does nothing, rather than putting the built-in guesses into
 * calendars nobody asked to change; an admin can still load them from the Holidays screen.
 */
@Component
public class HolidayYearStartJob {

    private static final Logger log = LoggerFactory.getLogger(HolidayYearStartJob.class);

    private final NationalHolidayService national;
    private final HolidayService holidays;
    private final PublicHolidayRepository holidayRepository;
    private final WorkHoursSettingsRepository settingsRepository;

    public HolidayYearStartJob(NationalHolidayService national, HolidayService holidays,
                               PublicHolidayRepository holidayRepository, WorkHoursSettingsRepository settingsRepository) {
        this.national = national;
        this.holidays = holidays;
        this.holidayRepository = holidayRepository;
        this.settingsRepository = settingsRepository;
        // A day-by-day fetch finishes in the background; fill schools in when it does.
        this.national.setOnLoaded(this::fillSchools);
    }

    @Scheduled(cron = "0 15 1 1 1 *", zone = "Africa/Accra")
    public void atStartOfYear() {
        run(LocalDate.now(CalendarService.ZONE).getYear());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void atStartup() {
        // Off the start-up thread: the fetch can take a few seconds and must never hold the service back.
        CompletableFuture.runAsync(() -> run(LocalDate.now(CalendarService.ZONE).getYear()));
    }

    public void run(int year) {
        try {
            NationalHolidayService.Outcome outcome = national.load(year, false);
            if (outcome.fromApi()) {
                fillSchools(year);
            } else if (outcome.note() != null) {
                log.info("Holidays for {} not loaded automatically: {}", year, outcome.note());
            }
        } catch (RuntimeException e) {
            log.warn("Start-of-year holiday load for {} failed: {}", year, e.getMessage());
        }
    }

    void fillSchools(int year) {
        try {
            NationalHolidayService.Outcome outcome = national.load(year, false);
            if (!outcome.fromApi()) return;
            for (String institution : schoolsUsingTheCalendar()) {
                if (holidayRepository.findByInstitutionCodeAndYear(institution, year).isEmpty()) {
                    int added = holidays.seedWith(institution, year, outcome).added();
                    log.info("Filled in {} holidays for {} for {}", added, institution, year);
                }
            }
        } catch (RuntimeException e) {
            log.warn("Filling schools' holiday calendars for {} failed: {}", year, e.getMessage());
        }
    }

    /** Schools that have set up holidays or working hours before - the ones that use this feature. */
    private Set<String> schoolsUsingTheCalendar() {
        Set<String> schools = new LinkedHashSet<>();
        for (PublicHoliday h : holidayRepository.findAll()) {
            if (h.getInstitutionCode() != null) schools.add(h.getInstitutionCode());
        }
        for (WorkHoursSettings s : settingsRepository.findAll()) {
            if (s.getInstitutionCode() != null) schools.add(s.getInstitutionCode());
        }
        return schools;
    }
}

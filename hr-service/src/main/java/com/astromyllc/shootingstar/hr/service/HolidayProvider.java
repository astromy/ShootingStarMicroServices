package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.exception.HolidayApiException;

import java.time.LocalDate;
import java.util.List;

/**
 * A service that can tell us Ghana's public holidays for a year. NationalHolidayService tries the
 * configured providers in order and keeps the first good answer.
 */
public interface HolidayProvider {

    /** @param statutoryDate what the law names; observedDate when staff are off (a provider's "substitute day") */
    record ApiHoliday(String name, LocalDate statutoryDate, LocalDate observedDate) {
    }

    /** @param mode YEAR (one request), RANGE (one request) or DAILY (one request per day) */
    record Fetched(List<ApiHoliday> holidays, String mode, int requests) {
    }

    /** MANSA, HOLIDAYSMAP - stored with the data so it can be shown later. */
    String id();

    /** For people: "Mansa API", "HolidaysMap". */
    String label();

    boolean isConfigured();

    /** The whole year in the cheapest way this provider offers. */
    Fetched fetchYear(int year);

    default boolean canFetchDaily() {
        return false;
    }

    default Fetched fetchDaily(int year) {
        throw new HolidayApiException(HolidayApiException.Kind.UNAVAILABLE, label() + " has no day-by-day lookup.");
    }
}

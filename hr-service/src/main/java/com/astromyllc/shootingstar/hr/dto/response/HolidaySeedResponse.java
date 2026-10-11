package com.astromyllc.shootingstar.hr.dto.response;

import java.util.List;

/**
 * @param source     the provider the dates came from (MANSA, HOLIDAYSMAP), or RULES when the built-in rules were used
 * @param provider   that provider's name for people ("Mansa API"), or null for RULES
 * @param sourceNote what happened (why the rules were used, a refresh that was skipped, ...); null when all is well
 */
public record HolidaySeedResponse(int added, List<HolidayResponse> holidays, String reminder,
                                  String source, String provider, String sourceNote) {
}

package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.HolidayRequest;
import com.astromyllc.shootingstar.hr.dto.response.HolidayResponse;
import com.astromyllc.shootingstar.hr.dto.response.HolidaySeedResponse;
import com.astromyllc.shootingstar.hr.exception.HrValidationException;
import com.astromyllc.shootingstar.hr.model.PublicHoliday;
import com.astromyllc.shootingstar.hr.repository.PublicHolidayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class HolidayService {

    private static final Set<String> TYPES = Set.of("STATUTORY", "DECLARED", "SCHOOL");

    private final PublicHolidayRepository repository;
    private final NationalHolidayService national;

    public HolidayService(PublicHolidayRepository repository, NationalHolidayService national) {
        this.repository = repository;
        this.national = national;
    }

    public List<HolidayResponse> list(String institutionCode, Integer year) {
        String code = requireInstitution(institutionCode);
        List<PublicHoliday> found = year == null
                ? repository.findByInstitutionCode(code)
                : repository.findByInstitutionCodeAndYear(code, year);
        List<HolidayResponse> out = new ArrayList<>();
        found.stream()
                .sorted(Comparator.comparing(PublicHoliday::getObservedDate))
                .forEach(h -> out.add(toResponse(h)));
        return out;
    }

    /**
     * Fills the school's calendar for a year from the national list (HolidaysMap, kept once for the whole
     * platform, or the built-in rules if that isn't available). It only ADDS: a holiday the school already has -
     * whether by the same name or on the same day - is left exactly as it is, so nothing an admin has changed
     * is ever overwritten.
     */
    public HolidaySeedResponse seed(String institutionCode, Integer year) {
        validateYear(year);
        return seedWith(institutionCode, year, national.load(year, false));
    }

    /** Asks HolidaysMap for a fresh copy of the year (limited to once a day), then adds anything missing. */
    public HolidaySeedResponse refreshFromApi(String institutionCode, Integer year) {
        validateYear(year);
        return seedWith(institutionCode, year, national.load(year, true));
    }

    public HolidaySeedResponse seedWith(String institutionCode, int year, NationalHolidayService.Outcome outcome) {
        String code = requireInstitution(institutionCode);
        boolean fromApi = outcome.fromApi();

        List<PublicHoliday> existing = repository.findByInstitutionCodeAndYear(code, year);
        Set<String> haveNames = new HashSet<>();
        Set<String> haveDates = new HashSet<>();
        for (PublicHoliday h : existing) {
            haveNames.add(nameKey(h.getName()));
            haveDates.add(h.getObservedDate());
        }

        List<PublicHoliday> toAdd = new ArrayList<>();
        for (NationalHolidayService.Day d : outcome.days()) {
            if (haveNames.contains(nameKey(d.name()))) continue;
            // A day the school already marks as a holiday wins, whatever it is called.
            if (haveDates.contains(d.observedDate().toString())) continue;
            PublicHoliday h = new PublicHoliday();
            h.setInstitutionCode(code);
            h.setYear(year);
            h.setName(d.name());
            h.setStatutoryDate(d.statutoryDate().toString());
            h.setObservedDate(d.observedDate().toString());
            h.setType("STATUTORY");
            h.setSource(fromApi ? outcome.source() : "RULES");
            toAdd.add(h);
            haveNames.add(nameKey(d.name()));
            haveDates.add(d.observedDate().toString());
        }
        repository.saveAll(toAdd);

        String reminder = "Eid ul-Fitr and Eid ul-Adha are only there if the holiday service listed them - check, and add them when the dates are announced. "
                + "If a holiday falls on a Tuesday, Wednesday or Thursday the President may move it to the Friday "
                + "before or the Monday after; check the Ministry of the Interior's notice and change the observed date.";
        return new HolidaySeedResponse(toAdd.size(), list(code, year), reminder, outcome.source(), outcome.provider(), outcome.note());
    }

    private static void validateYear(Integer year) {
        if (year == null || year < 2000 || year > 2100) {
            throw new HrValidationException("Choose a year between 2000 and 2100.");
        }
    }

    /** "Founders' Day" and "Founders Day" are the same holiday. */
    private static String nameKey(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    public HolidayResponse save(HolidayRequest r) {
        String code = requireInstitution(r.institutionCode());
        if (r.name() == null || r.name().isBlank()) throw new HrValidationException("Give the holiday a name.");
        if (r.observedDate() == null) throw new HrValidationException("Choose the date staff are off.");
        String type = r.type() == null || r.type().isBlank() ? "DECLARED" : r.type().trim().toUpperCase(Locale.ROOT);
        if (!TYPES.contains(type)) throw new HrValidationException("Holiday type must be statutory, declared or school.");

        PublicHoliday holiday;
        if (r.id() != null && !r.id().isBlank()) {
            holiday = repository.findById(r.id())
                    .filter(h -> code.equalsIgnoreCase(h.getInstitutionCode()))
                    .orElseThrow(() -> new HrValidationException("That holiday no longer exists."));
        } else {
            holiday = new PublicHoliday();
            holiday.setInstitutionCode(code);
        }

        String observed = r.observedDate().toString();
        for (PublicHoliday other : repository.findByInstitutionCode(code)) {
            if (observed.equals(other.getObservedDate()) && !other.getId().equals(holiday.getId())) {
                throw new HrValidationException("\"" + other.getName() + "\" is already the holiday on "
                        + r.observedDate() + ".");
            }
        }

        LocalDate statutory = r.statutoryDate() != null ? r.statutoryDate() : r.observedDate();
        holiday.setName(r.name().trim());
        holiday.setStatutoryDate(statutory.toString());
        holiday.setObservedDate(observed);
        holiday.setYear(statutory.getYear());
        holiday.setType(type);
        holiday.setNote(r.note() == null ? null : r.note().trim());
        // Added or edited by a person: from now on refreshes leave this row alone.
        holiday.setSource("ADMIN");
        return toResponse(repository.save(holiday));
    }

    public void delete(String institutionCode, String id) {
        String code = requireInstitution(institutionCode);
        PublicHoliday holiday = repository.findById(id == null ? "" : id)
                .filter(h -> code.equalsIgnoreCase(h.getInstitutionCode()))
                .orElseThrow(() -> new HrValidationException("That holiday no longer exists."));
        repository.delete(holiday);
    }

    private static HolidayResponse toResponse(PublicHoliday h) {
        LocalDate observed = LocalDate.parse(h.getObservedDate());
        LocalDate statutory = h.getStatutoryDate() == null ? observed : LocalDate.parse(h.getStatutoryDate());
        return new HolidayResponse(h.getId(), h.getYear(), h.getName(), statutory, observed,
                observed.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH), h.getType(), h.getNote(),
                h.getSource());
    }

    static String requireInstitution(String code) {
        if (code == null || code.isBlank()) {
            throw new HrValidationException("Your account isn't linked to a school. Contact your administrator.");
        }
        return code.trim();
    }
}

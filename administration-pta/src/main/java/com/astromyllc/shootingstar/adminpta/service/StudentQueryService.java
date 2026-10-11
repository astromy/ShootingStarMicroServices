package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.StudentPageRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.LabelCount;
import com.astromyllc.shootingstar.adminpta.dto.response.PagedResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentRowResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSummaryResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.util.PictureCodec;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Counts, pages and single records straight from the in-memory student list
 * ({@link StudentUtil#studentsGlobalList}) - the list stays the source of truth and nothing is
 * copied or cached here, so an admission, an edit or a class move shows up on the next request.
 *
 * The browser never needs the whole school: the dashboard gets {@link #summary counts},
 * the tables get {@link #page one page}, and a record (with parents, picture, certificate) is
 * fetched for one student at a time.
 */
@Service
public class StudentQueryService {

    static final int DEFAULT_PAGE_SIZE = 50;
    static final int MAX_PAGE_SIZE = 200;
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private final StudentUtil studentUtil;

    public StudentQueryService(StudentUtil studentUtil) {
        this.studentUtil = studentUtil;
    }

    // ── Public API (reads the live list) ────────────────────────────────────

    public StudentSummaryResponse summary(String institutionCode) {
        return summarize(snapshot(), institutionCode, LocalDate.now().getYear());
    }

    public PagedResponse<StudentRowResponse> page(StudentPageRequest request) {
        return page(snapshot(), request);
    }

    /** The whole record of one student (parents, subjects, picture, certificate) - for the edit screen. */
    public Optional<StudentsResponse> detail(String institutionCode, String studentId) {
        return find(snapshot(), institutionCode, studentId).map(studentUtil::mapStudent_ToStudentResponse);
    }

    /** A student's picture as an image; the small version unless the full one is asked for. */
    public Optional<PictureResult> photo(String institutionCode, String studentId, boolean thumbnail) {
        Optional<Students> student = find(snapshot(), institutionCode, studentId);
        if (student.isEmpty()) return Optional.empty();
        Students s = student.get();
        String stored = s.getPicture();
        if (stored == null || stored.isBlank()) return Optional.empty();

        // thumbnailOf() creates the small version once per picture and keeps it in memory
        PictureCodec.Decoded decoded = PictureCodec.decode(thumbnail ? StudentUtil.thumbnailOf(s) : stored);
        if (decoded == null) return Optional.empty();
        String tag = "\"" + Integer.toHexString(stored.hashCode()) + (thumbnail ? "-t" : "-f") + "\"";
        return Optional.of(new PictureResult(decoded, tag));
    }

    /** A picture, with a tag that changes when the stored picture changes (so browsers can cache it). */
    public record PictureResult(PictureCodec.Decoded picture, String eTag) {
    }

    // ── Engine (works on a snapshot, so it is simple to test) ───────────────

    private static Students[] snapshot() {
        List<Students> live = StudentUtil.studentsGlobalList;
        if (live == null) return new Students[0];
        // toArray copies in one go; other requests may be admitting students while this one reads
        return live.toArray(new Students[0]);
    }

    static Optional<Students> find(Students[] all, String institutionCode, String studentId) {
        if (blank(institutionCode) || blank(studentId)) return Optional.empty();
        for (Students s : all) {
            if (s != null && same(s.getInstitutionCode(), institutionCode) && same(s.getStudentId(), studentId)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    static StudentSummaryResponse summarize(Students[] all, String institutionCode, int year) {
        long total = 0, active = 0, suspended = 0, newThisYear = 0, male = 0, female = 0;
        Map<String, Long> byClass = new HashMap<>();
        Map<String, Long> byStatus = new HashMap<>();
        Map<String, Long> byLocality = new HashMap<>();
        long[] byMonth = new long[12];
        Map<Integer, Long> byYear = new TreeMap<>();

        if (!blank(institutionCode)) {
            for (Students s : all) {
                if (s == null || !same(s.getInstitutionCode(), institutionCode)) continue;
                total++;
                if (same(s.getStatus(), "active")) active++;
                if (same(s.getStatus(), "suspended")) suspended++;
                if (same(s.getGender(), "male")) male++;
                if (same(s.getGender(), "female")) female++;
                byClass.merge(orUnknown(s.getStudentClass()), 1L, Long::sum);
                byStatus.merge(orUnknown(s.getStatus()), 1L, Long::sum);
                byLocality.merge(orUnknown(s.getResidentialLocality()), 1L, Long::sum);
                LocalDate admitted = s.getDateOfAdmission();
                if (admitted != null) {
                    byYear.merge(admitted.getYear(), 1L, Long::sum);
                    if (admitted.getYear() == year) {
                        newThisYear++;
                        byMonth[admitted.getMonthValue() - 1]++;
                    }
                }
            }
        }

        List<LabelCount> months = new ArrayList<>();
        for (int m = 0; m < 12; m++) {
            if (byMonth[m] > 0) months.add(new LabelCount(MONTHS[m], byMonth[m]));
        }
        List<LabelCount> years = new ArrayList<>();
        byYear.forEach((y, n) -> years.add(new LabelCount(String.valueOf(y), n)));

        return new StudentSummaryResponse(
                total, active, suspended, newThisYear, year,
                biggestFirst(byClass, Integer.MAX_VALUE),
                List.of(new LabelCount("Male", male), new LabelCount("Female", female)),
                biggestFirst(byStatus, Integer.MAX_VALUE),
                biggestFirst(byLocality, 10),
                months,
                years);
    }

    static PagedResponse<StudentRowResponse> page(Students[] all, StudentPageRequest r) {
        int size = r.getSize() == null ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(r.getSize(), MAX_PAGE_SIZE));
        int page = r.getPage() == null ? 0 : Math.max(0, r.getPage());
        if (blank(r.getInstitutionCode())) {
            return new PagedResponse<>(0, 0, page, size, List.of());
        }

        List<Students> matching = new ArrayList<>();
        for (Students s : all) {
            if (s == null || !same(s.getInstitutionCode(), r.getInstitutionCode())) continue;
            if (!blank(r.getStudentClass()) && !same(s.getStudentClass(), r.getStudentClass())) continue;
            if (!blank(r.getStatus()) && !same(s.getStatus(), r.getStatus())) continue;
            if (!blank(r.getGender()) && !same(s.getGender(), r.getGender())) continue;
            matching.add(s);
        }
        long total = matching.size();

        List<Students> shown = matching;
        String[] terms = searchTerms(r.getSearch());
        if (terms.length > 0) {
            shown = new ArrayList<>();
            for (Students s : matching) {
                String haystack = haystack(s);
                boolean matchesAll = true;
                for (String term : terms) {
                    if (!haystack.contains(term)) {
                        matchesAll = false;
                        break;
                    }
                }
                if (matchesAll) shown.add(s);
            }
        }
        long filtered = shown.size();

        shown.sort(comparator(r.getSortBy(), r.getSortDir()));

        long from = (long) page * size;
        List<StudentRowResponse> rows = new ArrayList<>();
        for (long i = from; i < Math.min(filtered, from + size); i++) {
            rows.add(row(shown.get((int) i)));
        }
        return new PagedResponse<>(total, filtered, page, size, rows);
    }

    static StudentRowResponse row(Students s) {
        String picture = s.getPicture();
        return new StudentRowResponse(
                s.getStudentId(), s.getFirstName(), s.getOtherName(), s.getLastName(), s.getGender(),
                s.getStudentClass(), s.getDateOfBirth(), s.getDateOfAdmission(), s.getPlaceOfBirth(),
                s.getCountryOfBirth(),
                // the screens show nationality as the country of birth (the record has no separate field)
                s.getCountryOfBirth(),
                s.getDenomination(), s.getStatus(),
                picture != null && picture.length() > 100);
    }

    // ── Sorting and searching ───────────────────────────────────────────────

    private static Comparator<Students> comparator(String sortBy, String sortDir) {
        Comparator<Students> primary;
        switch (sortBy == null ? "" : sortBy) {
            case "studentId" -> primary = text(Students::getStudentId);
            case "firstName" -> primary = text(Students::getFirstName);
            case "otherName" -> primary = text(Students::getOtherName);
            case "gender" -> primary = text(Students::getGender);
            case "studentClass" -> primary = text(Students::getStudentClass);
            case "status" -> primary = text(Students::getStatus);
            case "placeOfBirth" -> primary = text(Students::getPlaceOfBirth);
            case "countryOfBirth", "nationality" -> primary = text(Students::getCountryOfBirth);
            case "denomination" -> primary = text(Students::getDenomination);
            case "dateOfBirth" -> primary = Comparator.comparing(Students::getDateOfBirth, Comparator.nullsLast(Comparator.naturalOrder()));
            case "dateOfAdmission" -> primary = Comparator.comparing(Students::getDateOfAdmission, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> primary = text(Students::getLastName);
        }
        if ("desc".equalsIgnoreCase(sortDir)) primary = primary.reversed();
        // same order every time, whatever the sort column holds
        return primary.thenComparing(text(Students::getLastName)).thenComparing(text(Students::getFirstName))
                .thenComparing(text(Students::getStudentId));
    }

    private static Comparator<Students> text(java.util.function.Function<Students, String> field) {
        return Comparator.comparing(s -> {
            String v = field.apply(s);
            return v == null ? "" : v.toLowerCase(Locale.ROOT);
        });
    }

    private static String[] searchTerms(String search) {
        if (blank(search)) return new String[0];
        return search.trim().toLowerCase(Locale.ROOT).split("\\s+");
    }

    private static String haystack(Students s) {
        StringBuilder b = new StringBuilder(160);
        for (String part : new String[]{s.getStudentId(), s.getFirstName(), s.getOtherName(), s.getLastName(), s.getGender(),
                s.getStudentClass(), s.getPlaceOfBirth(), s.getCountryOfBirth(), s.getDenomination(), s.getStatus(),
                s.getDateOfBirth() == null ? null : s.getDateOfBirth().toString(),
                s.getDateOfAdmission() == null ? null : s.getDateOfAdmission().toString()}) {
            if (part != null) b.append(part).append(' ');
        }
        return b.toString().toLowerCase(Locale.ROOT);
    }

    // ── Small helpers ───────────────────────────────────────────────────────

    private static List<LabelCount> biggestFirst(Map<String, Long> counts, int limit) {
        List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()));
        List<LabelCount> out = new ArrayList<>();
        for (Map.Entry<String, Long> e : entries) {
            if (out.size() >= limit) break;
            out.add(new LabelCount(e.getKey(), e.getValue()));
        }
        return out;
    }

    private static String orUnknown(String value) {
        return value == null || value.isBlank() ? "Unknown" : value.trim();
    }

    private static boolean same(String a, String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}

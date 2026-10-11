package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.StaffPageRequest;
import com.astromyllc.shootingstar.hr.dto.response.LabelCount;
import com.astromyllc.shootingstar.hr.dto.response.PagedResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffRowResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffSummaryResponse;
import com.astromyllc.shootingstar.hr.model.DesignationList;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.model.StaffSubjects;
import com.astromyllc.shootingstar.hr.utils.StaffPhoto;
import com.astromyllc.shootingstar.hr.utils.StaffUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Counts, pages and single records straight from the in-memory staff list
 * ({@link StaffUtil#staffGlobalList}). Nothing is copied or cached here, so a new hire or an edit
 * shows up on the next request.
 */
@Service
public class StaffQueryService {

    static final int DEFAULT_PAGE_SIZE = 50;
    static final int MAX_PAGE_SIZE = 200;

    private final StaffUtil staffUtil;

    public StaffQueryService(StaffUtil staffUtil) {
        this.staffUtil = staffUtil;
    }

    // ── Public API (reads the live list) ────────────────────────────────────

    public StaffSummaryResponse summary(String institutionCode) {
        return summarize(snapshot(), institutionCode);
    }

    public PagedResponse<StaffRowResponse> page(StaffPageRequest request) {
        return page(snapshot(), request);
    }

    /** The whole record of one staff member (picture, documents, dependants, records). */
    public Optional<StaffResponse> detail(String institutionCode, String staffCode) {
        Optional<Staff> staff = find(snapshot(), institutionCode, staffCode);
        if (staff.isEmpty()) return Optional.empty();
        try {
            return Optional.of(staffUtil.mapStaff_ToStaffResponse(staff.get()));
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException("Could not read the staff record for " + staffCode, e);
        }
    }

    /**
     * Everyone in a school with the same fields as always, except the picture and the contents of the
     * uploaded documents - the two things that make a staff list heavy. For screens that only fill a
     * drop-down or show names.
     */
    public List<StaffResponse> listWithoutFiles(String institutionCode) {
        List<StaffResponse> out = new ArrayList<>();
        if (blank(institutionCode)) return out;
        for (Staff staff : snapshot()) {
            if (staff == null || !same(staff.getInstitutionCode(), institutionCode)) continue;
            try {
                StaffResponse response = staffUtil.mapStaff_ToStaffResponse(staff);
                response.setStaffPicture(null);
                if (response.getStaffDocuments() != null) {
                    response.getStaffDocuments().forEach(d -> d.setDocument(null));
                }
                out.add(response);
            } catch (URISyntaxException | IOException e) {
                throw new IllegalStateException("Could not read the staff record for " + staff.getStaffCode(), e);
            }
        }
        return out;
    }

    /** A person's picture as an image; the small version unless the full one is asked for. */
    public Optional<PictureResult> photo(String institutionCode, String staffCode, boolean thumbnail) {
        Optional<Staff> found = find(snapshot(), institutionCode, staffCode);
        if (found.isEmpty()) return Optional.empty();
        String stored = found.get().getStaffPicture();
        if (stored == null || stored.isBlank()) return Optional.empty();
        StaffPhoto.Decoded decoded = thumbnail ? StaffPhoto.thumbnail(found.get().getStaffCode(), stored) : StaffPhoto.decode(stored);
        if (decoded == null) return Optional.empty();
        String tag = "\"" + Integer.toHexString(stored.hashCode()) + (thumbnail ? "-t" : "-f") + "\"";
        return Optional.of(new PictureResult(decoded, tag));
    }

    /** A picture, with a tag that changes when the stored picture changes (so browsers can cache it). */
    public record PictureResult(StaffPhoto.Decoded picture, String eTag) {
    }

    // ── Engine (works on a snapshot, so it is simple to test) ───────────────

    private static Staff[] snapshot() {
        List<Staff> live = StaffUtil.staffGlobalList;
        if (live == null) return new Staff[0];
        // toArray copies in one go; other requests may be adding staff while this one reads
        return live.toArray(new Staff[0]);
    }

    static Optional<Staff> find(Staff[] all, String institutionCode, String staffCode) {
        if (blank(institutionCode) || blank(staffCode)) return Optional.empty();
        for (Staff s : all) {
            if (s != null && same(s.getInstitutionCode(), institutionCode) && same(s.getStaffCode(), staffCode)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    static StaffSummaryResponse summarize(Staff[] all, String institutionCode) {
        long total = 0, male = 0, female = 0;
        Map<String, Long> byDesignation = new HashMap<>();
        Map<String, Long> byDepartment = new HashMap<>();
        Set<String> designations = new HashSet<>();

        if (!blank(institutionCode)) {
            for (Staff s : all) {
                if (s == null || !same(s.getInstitutionCode(), institutionCode)) continue;
                total++;
                if (same(s.getGender(), "male")) male++;
                if (same(s.getGender(), "female")) female++;
                byDesignation.merge(orUnknown(s.getDesignation()), 1L, Long::sum);
                if (!blank(s.getDesignation())) designations.add(s.getDesignation().trim().toLowerCase(Locale.ROOT));

                // A person counts once in each department they hold a designation in
                Set<String> departments = new HashSet<>();
                if (s.getStaffDesignations() != null) {
                    for (DesignationList d : s.getStaffDesignations()) {
                        if (d != null && !blank(d.getDepartment()) && same(d.getInstitutionCode(), s.getInstitutionCode())) {
                            departments.add(d.getDepartment().trim());
                        }
                    }
                }
                if (departments.isEmpty()) departments.add("Unknown");
                for (String department : departments) byDepartment.merge(department, 1L, Long::sum);
            }
        }
        return new StaffSummaryResponse(
                total, designations.size(),
                List.of(new LabelCount("Male", male), new LabelCount("Female", female)),
                biggestFirst(byDesignation),
                biggestFirst(byDepartment));
    }

    static PagedResponse<StaffRowResponse> page(Staff[] all, StaffPageRequest r) {
        int size = r.getSize() == null ? DEFAULT_PAGE_SIZE : Math.max(1, Math.min(r.getSize(), MAX_PAGE_SIZE));
        int page = r.getPage() == null ? 0 : Math.max(0, r.getPage());
        if (blank(r.getInstitutionCode())) {
            return new PagedResponse<>(0, 0, page, size, List.of());
        }

        List<Staff> inSchool = new ArrayList<>();
        for (Staff s : all) {
            if (s != null && same(s.getInstitutionCode(), r.getInstitutionCode())) inSchool.add(s);
        }
        long total = inSchool.size();

        List<Staff> shown = inSchool;
        String[] terms = searchTerms(r.getSearch());
        if (terms.length > 0) {
            shown = new ArrayList<>();
            for (Staff s : inSchool) {
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
        List<StaffRowResponse> rows = new ArrayList<>();
        for (long i = from; i < Math.min(filtered, from + size); i++) {
            rows.add(row(shown.get((int) i)));
        }
        return new PagedResponse<>(total, filtered, page, size, rows);
    }

    static StaffRowResponse row(Staff s) {
        List<StaffRowResponse.SubjectLine> subjects = new ArrayList<>();
        if (s.getStaffSubjects() != null) {
            for (StaffSubjects subject : s.getStaffSubjects()) {
                if (subject != null && same(subject.getInstitutionCode(), s.getInstitutionCode())) {
                    subjects.add(new StaffRowResponse.SubjectLine(subject.getSubject(), subject.getSubjectClass()));
                }
            }
        }
        String picture = s.getStaffPicture();
        return new StaffRowResponse(
                s.getStaffCode(), s.getFirstNames(), s.getLastName(), s.getGender(), s.getContact1(), s.getStaffEmail(),
                s.getDateOfEmployment(), s.getDesignation(), s.getNationality(), subjects,
                picture != null && picture.length() > 100);
    }

    // ── Sorting and searching ───────────────────────────────────────────────

    private static Comparator<Staff> comparator(String sortBy, String sortDir) {
        Comparator<Staff> primary;
        switch (sortBy == null ? "" : sortBy) {
            case "staffCode" -> primary = text(Staff::getStaffCode);
            case "firstNames" -> primary = text(Staff::getFirstNames);
            case "name" -> primary = text(s -> (s.getFirstNames() == null ? "" : s.getFirstNames()) + " " + (s.getLastName() == null ? "" : s.getLastName()));
            case "gender" -> primary = text(Staff::getGender);
            case "contact1" -> primary = text(Staff::getContact1);
            case "staffEmail" -> primary = text(Staff::getStaffEmail);
            case "designation" -> primary = text(Staff::getDesignation);
            case "nationality" -> primary = text(Staff::getNationality);
            case "dateOfEmployment" -> primary = Comparator.comparing(Staff::getDateOfEmployment, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> primary = text(Staff::getLastName);
        }
        if ("desc".equalsIgnoreCase(sortDir)) primary = primary.reversed();
        return primary.thenComparing(text(Staff::getLastName)).thenComparing(text(Staff::getFirstNames))
                .thenComparing(text(Staff::getStaffCode));
    }

    private static Comparator<Staff> text(Function<Staff, String> field) {
        return Comparator.comparing(s -> {
            String v = field.apply(s);
            return v == null ? "" : v.toLowerCase(Locale.ROOT);
        });
    }

    private static String[] searchTerms(String search) {
        if (blank(search)) return new String[0];
        return search.trim().toLowerCase(Locale.ROOT).split("\\s+");
    }

    private static String haystack(Staff s) {
        StringBuilder b = new StringBuilder(200);
        for (String part : new String[]{s.getStaffCode(), s.getFirstNames(), s.getLastName(), s.getGender(), s.getContact1(),
                s.getStaffEmail(), s.getDesignation(), s.getNationality(),
                s.getDateOfEmployment() == null ? null : s.getDateOfEmployment().toString()}) {
            if (part != null) b.append(part).append(' ');
        }
        if (s.getStaffSubjects() != null) {
            for (StaffSubjects subject : s.getStaffSubjects()) {
                if (subject != null) b.append(subject.getSubject()).append(' ').append(subject.getSubjectClass()).append(' ');
            }
        }
        return b.toString().toLowerCase(Locale.ROOT);
    }

    // ── Small helpers ───────────────────────────────────────────────────────

    private static List<LabelCount> biggestFirst(Map<String, Long> counts) {
        List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()));
        List<LabelCount> out = new ArrayList<>();
        for (Map.Entry<String, Long> e : entries) out.add(new LabelCount(e.getKey(), e.getValue()));
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

package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.dto.request.AcademicReportRequest;
import com.astromyllc.shootingstar.academics.model.Assessment;
import com.astromyllc.shootingstar.academics.model.PublishedReport;
import com.astromyllc.shootingstar.academics.repository.PublishedReportRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Which class results have been published to parents. Kept in memory like the other global lists;
// the database copy is only read at startup.
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishedReportUtil {
    private static final Set<String> publishedKeys = ConcurrentHashMap.newKeySet();
    private final PublishedReportRepository publishedReportRepository;

    public static String key(String institutionCode, String studentClass, String term, String academicYear) {
        return String.join("|", normalise(institutionCode), normalise(studentClass),
                normalise(term), normalise(academicYear));
    }

    private static String normalise(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    public static boolean isPublished(Assessment assessment) {
        return publishedKeys.contains(key(assessment.getInstitutionCode(), assessment.getStudentClass(),
                assessment.getTerm(), assessment.getAcademicYear()));
    }

    // Publishing only makes sense once the results exist; otherwise they would go live the moment
    // they are generated later, without anyone publishing them.
    public static boolean hasGeneratedResults(AcademicReportRequest request, List<Assessment> assessments) {
        if (assessments == null) return false;
        String wanted = key(request.getInstitutionCode(), request.getTargetClass(),
                request.getTerm(), request.getAcademicYear());
        return assessments.stream().anyMatch(a -> wanted.equals(key(a.getInstitutionCode(),
                a.getStudentClass(), a.getTerm(), a.getAcademicYear())));
    }

    @PostConstruct
    private void fetchAllPublishedReports() {
        publishedReportRepository.findAll().forEach(p ->
                publishedKeys.add(key(p.getInstitutionCode(), p.getStudentClass(), p.getTerm(), p.getAcademicYear())));
        log.info("Published Reports List populated with {} records", publishedKeys.size());
    }

    // Publishing again is harmless: an already published class/term/year is left as it is.
    public void publish(AcademicReportRequest request) {
        String key = key(request.getInstitutionCode(), request.getTargetClass(),
                request.getTerm(), request.getAcademicYear());
        if (publishedKeys.contains(key)) return;

        publishedReportRepository.save(PublishedReport.builder()
                .institutionCode(request.getInstitutionCode())
                .studentClass(request.getTargetClass())
                .term(request.getTerm())
                .academicYear(request.getAcademicYear())
                .publishedAt(LocalDateTime.now())
                .build());
        publishedKeys.add(key);
    }
}
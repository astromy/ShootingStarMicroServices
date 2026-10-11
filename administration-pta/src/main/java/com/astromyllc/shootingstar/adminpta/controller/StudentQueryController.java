package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentDetailRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentPageRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.PagedResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentRowResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSummaryResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.service.StudentQueryService;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Light-weight reads for the web screens: counts, one page of students, one student,
 * one picture. The school code arrives from the ORB proxy, which takes it from the signed-in user.
 */
@RestController
public class StudentQueryController {

    private final StudentQueryService queries;
    private final StudentServiceInterface students;

    public StudentQueryController(StudentQueryService queries, StudentServiceInterface students) {
        this.queries = queries;
        this.students = students;
    }

    /** Counts for the dashboards. Body: {"val": "<school code>"} */
    @PostMapping("/api/administration-pta/dashboard-summary")
    public ResponseEntity<StudentSummaryResponse> dashboardSummary(@RequestBody SingleStringRequest school) {
        return ResponseEntity.ok(queries.summary(school.getVal()));
    }

    /** One page of students. No pictures, certificates or parents in it. */
    @PostMapping("/api/administration-pta/getStudentsPage")
    public ResponseEntity<PagedResponse<StudentRowResponse>> studentsPage(@RequestBody StudentPageRequest request) {
        return ResponseEntity.ok(queries.page(request));
    }

    /**
     * Same as getStudentsByDynamicData, but without each student's picture and birth certificate -
     * the two fields that make a student record heavy. Parents and subjects are still included.
     */
    @PostMapping("/api/administration-pta/getStudentsByDynamicDataWithoutImages")
    public ResponseEntity<Optional<List<StudentsResponse>>> studentsByDynamicDataWithoutImages(@RequestBody DynamicStringRequest request) {
        Optional<List<StudentsResponse>> result = students.fetchStudentsByDynamicData(request);
        // the responses are built fresh for this request, so clearing the two fields changes no stored record
        result.ifPresent(list -> list.forEach(s -> {
            s.setPicture(null);
            s.setBirthCert(null);
        }));
        return ResponseEntity.ok(result);
    }

    /**
     * One student for the edit screen: bio data, parents and subjects - but not the picture or the birth
     * certificate, which are the heavy parts. The picture is shown from student-photo; for the certificate
     * only its file name is returned (so the screen can say one is on file). Saving without a new picture
     * or certificate leaves the stored ones untouched.
     */
    @PostMapping("/api/administration-pta/getStudentDetail")
    public ResponseEntity<StudentsResponse> studentDetail(@RequestBody StudentDetailRequest request) {
        return queries.detail(request.getInstitutionCode(), request.getStudentId())
                .map(student -> {
                    student.setPicture(null);
                    student.setBirthCert(certificateName(student.getBirthCert()));
                    return ResponseEntity.ok(student);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** "name.pdf_application/pdf_<base64>" -> "name.pdf"; null when no certificate is stored. */
    private static String certificateName(String stored) {
        if (stored == null || stored.isBlank()) return null;
        int marker = stored.indexOf("_application/pdf_");
        if (marker < 0) return stored.startsWith("data:application/pdf") ? "birth-certificate.pdf" : (stored.length() > 100 ? null : stored);
        return marker == 0 ? "birth-certificate.pdf" : stored.substring(0, marker);
    }

    /**
     * A student's picture as an image file. Small version by default; size=full for the original.
     * Browsers keep it (ETag + max-age), so a picture is downloaded once, not on every page load.
     */
    @GetMapping("/api/administration-pta/student-photo/{institutionCode}/{studentId}")
    public ResponseEntity<byte[]> studentPhoto(@PathVariable String institutionCode,
                                               @PathVariable String studentId,
                                               @RequestParam(value = "size", defaultValue = "thumb") String size,
                                               @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {
        Optional<StudentQueryService.PictureResult> found = queries.photo(institutionCode, studentId, !"full".equalsIgnoreCase(size));
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        StudentQueryService.PictureResult result = found.get();
        CacheControl cache = CacheControl.maxAge(1, TimeUnit.HOURS).cachePrivate().mustRevalidate();
        if (result.eTag().equals(ifNoneMatch)) {
            return ResponseEntity.status(304).eTag(result.eTag()).cacheControl(cache).build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.picture().contentType()))
                .eTag(result.eTag())
                .cacheControl(cache)
                .body(result.picture().bytes());
    }
}

package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.utils.BackendClient;
import com.astromyllc.astroorb.utils.TenantResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The student and staff tables and pictures: one page at a time, one record at a time.
 *
 * Instead of sending every record (with pictures and certificates) to the browser, the tables
 * ask for a page of rows, a record is fetched when somebody opens it, and a picture is an image
 * the browser keeps.
 *
 * As with the other screens built on TenantResolver, the school comes from the signed-in user's
 * login and is written into every request here - whatever the browser sends for it is ignored.
 */
@Controller
@ResponseBody
public class ListScreensController {

    private static final Logger log = LoggerFactory.getLogger(ListScreensController.class);
    private static final Duration LIST_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration PICTURE_TIMEOUT = Duration.ofSeconds(15);

    // The only things a browser may ask a page to be filtered / sorted by
    private static final Set<String> STUDENT_PAGE_FIELDS = Set.of("studentClass", "status", "gender", "search", "sortBy", "sortDir", "page", "size");
    private static final Set<String> STAFF_PAGE_FIELDS = Set.of("search", "sortBy", "sortDir", "page", "size");

    private static final String NO_SCHOOL = "{\"error\":\"Your account isn't linked to a school. Contact your administrator.\"}";

    private final BackendClient backend;

    public ListScreensController(BackendClient backend) {
        this.backend = backend;
    }

    // ── Students ────────────────────────────────────────────────────────────

    @PostMapping("getStudentsPage")
    public ResponseEntity<String> studentsPage(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(principal, body, STUDENT_PAGE_FIELDS, "institutionCode", "/api/administration-pta/getStudentsPage");
    }

    /** body: {"studentId": "..."} */
    @PostMapping("getStudentDetail")
    public ResponseEntity<String> studentDetail(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(principal, body, Set.of("studentId"), "institutionCode", "/api/administration-pta/getStudentDetail");
    }

    /** A student's picture. size=thumb (default) or size=full. */
    @GetMapping("student-photo/{studentId}")
    public ResponseEntity<byte[]> studentPhoto(@PathVariable String studentId,
                                               @RequestParam(value = "size", defaultValue = "thumb") String size,
                                               @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch,
                                               @AuthenticationPrincipal OAuth2User principal) {
        return picture(principal, "/api/administration-pta/student-photo/", studentId, size, ifNoneMatch);
    }

    // ── Staff ───────────────────────────────────────────────────────────────

    @PostMapping("getStaffPage")
    public ResponseEntity<String> staffPage(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(principal, body, STAFF_PAGE_FIELDS, "institutionCode", "/api/hr/getStaffPage");
    }

    /** body: {"staffCode": "..."} */
    @PostMapping("getStaffDetail")
    public ResponseEntity<String> staffDetail(@RequestBody Map<String, Object> body, @AuthenticationPrincipal OAuth2User principal) {
        return forward(principal, body, Set.of("staffCode"), "institutionCode", "/api/hr/getStaffDetail");
    }

    /** A staff member's picture. size=thumb (default) or size=full. */
    @GetMapping("staff-photo/{staffCode}")
    public ResponseEntity<byte[]> staffPhoto(@PathVariable String staffCode,
                                             @RequestParam(value = "size", defaultValue = "thumb") String size,
                                             @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch,
                                             @AuthenticationPrincipal OAuth2User principal) {
        return picture(principal, "/api/hr/staff-photo/", staffCode, size, ifNoneMatch);
    }

    // ── Plumbing ────────────────────────────────────────────────────────────

    /** Sends only the allowed fields of the body to the service, with the school filled in from the login. */
    private ResponseEntity<String> forward(OAuth2User principal, Map<String, Object> body, Set<String> allowed, String schoolField, String path) {
        Optional<String> school = TenantResolver.institutionCode(principal);
        if (school.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).contentType(MediaType.APPLICATION_JSON).body(NO_SCHOOL);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        if (body != null) {
            body.forEach((key, value) -> {
                if (allowed.contains(key)) out.put(key, value);
            });
        }
        out.put(schoolField, school.get());
        try {
            BackendClient.Reply reply = backend.post(path, out, LIST_TIMEOUT);
            return ResponseEntity.status(reply.status()).contentType(MediaType.APPLICATION_JSON).body(reply.body());
        } catch (BackendClient.Unavailable e) {
            log.error("List proxy error for {}: {}", path, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"The service couldn't be reached. Try again.\"}");
        }
    }

    private ResponseEntity<byte[]> picture(OAuth2User principal, String basePath, String id, String size, String ifNoneMatch) {
        Optional<String> school = TenantResolver.institutionCode(principal);
        if (school.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String path = basePath + encode(school.get()) + "/" + encode(id) + "?size=" + ("full".equalsIgnoreCase(size) ? "full" : "thumb");
        try {
            BackendClient.Image image = backend.getImage(path, ifNoneMatch, PICTURE_TIMEOUT);
            if (image.status() == 304) {
                return withCaching(ResponseEntity.status(HttpStatus.NOT_MODIFIED), image).build();
            }
            if (image.status() != 200 || image.bytes() == null) {
                return ResponseEntity.status(image.status() == 404 ? HttpStatus.NOT_FOUND : HttpStatus.BAD_GATEWAY).build();
            }
            ResponseEntity.BodyBuilder ok = withCaching(ResponseEntity.ok(), image);
            if (image.contentType() != null) ok.contentType(MediaType.parseMediaType(image.contentType()));
            return ok.body(image.bytes());
        } catch (BackendClient.Unavailable e) {
            log.error("Picture proxy error for {}: {}", path, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private static ResponseEntity.BodyBuilder withCaching(ResponseEntity.BodyBuilder builder, BackendClient.Image image) {
        if (image.eTag() != null) builder.eTag(image.eTag());
        if (image.cacheControl() != null) builder.header(HttpHeaders.CACHE_CONTROL, image.cacheControl());
        return builder;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}

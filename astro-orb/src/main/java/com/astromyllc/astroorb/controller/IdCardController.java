package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.IdCardStudentsRequest;
import com.astromyllc.astroorb.utils.CrestPaletteService;
import com.astromyllc.astroorb.utils.TenantResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Data for Administration -> ID Cards.
 *
 * Multi-tenancy: every endpoint resolves the school from the signed-in user's
 * token (TenantResolver) and ignores any school sent by the browser. Students
 * are loaded by institution and filtered by class here - NOT through
 * administration-pta's getStudentsByClass, which matches on class name only
 * and would mix students from every school that has a class with that name.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class IdCardController {

    private static final int MAX_CLASSES_PER_REQUEST = 20;
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper;
    private final CrestPaletteService crestPaletteService;

    @Value("${gateway.host}")
    private String backendserve;

    /** The signed-in user's school details, its crest colours, and its classes. */
    @ResponseBody
    @PostMapping("idcards/context")
    public ResponseEntity<Object> getContext(@AuthenticationPrincipal OAuth2User principal) {
        Optional<String> institutionCode = TenantResolver.institutionCode(principal);
        if (institutionCode.isEmpty()) {
            return message(HttpStatus.FORBIDDEN, "Your account isn't linked to a school. Contact your administrator.");
        }

        JsonNode institution = post("/api/setup/getInstitutionByCode", Map.of("val", institutionCode.get()));
        if (institution == null || !institution.isObject()) {
            return message(HttpStatus.BAD_GATEWAY, "Couldn't load your school's details. Try again.");
        }

        String crest = text(institution, "crest");
        CrestPaletteService.Palette palette = crestPaletteService.paletteFor(institutionCode.get(), crest);

        Map<String, Object> school = new LinkedHashMap<>();
        for (String field : List.of("name", "slogan", "city", "region", "postalAddress",
                "email", "website", "contact1", "contact2", "crest", "headSignature")) {
            school.put(field, text(institution, field));
        }

        List<String> classes = StreamSupport.stream(institution.path("classList").spliterator(), false)
                .map(c -> text(c, "name"))
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("institution", school);
        body.put("palette", palette);
        body.put("classes", classes);
        return ResponseEntity.ok(body);
    }

    /** Current students in the chosen classes of the signed-in user's school. */
    @ResponseBody
    @PostMapping("idcards/students")
    public ResponseEntity<Object> getStudents(@RequestBody IdCardStudentsRequest request,
                                              @AuthenticationPrincipal OAuth2User principal) {
        Optional<String> institutionCode = TenantResolver.institutionCode(principal);
        if (institutionCode.isEmpty()) {
            return message(HttpStatus.FORBIDDEN, "Your account isn't linked to a school. Contact your administrator.");
        }

        Set<String> wantedClasses = Optional.ofNullable(request.getClassNames()).orElse(List.of()).stream()
                .filter(name -> name != null && !name.isBlank())
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        if (wantedClasses.isEmpty()) {
            return message(HttpStatus.BAD_REQUEST, "Choose at least one class.");
        }
        if (wantedClasses.size() > MAX_CLASSES_PER_REQUEST) {
            return message(HttpStatus.BAD_REQUEST,
                    "Choose up to " + MAX_CLASSES_PER_REQUEST + " classes at a time.");
        }

        JsonNode all = post("/api/administration-pta/getStudentsByInstitution", Map.of("val", institutionCode.get()));
        if (all == null || !all.isArray()) {
            return message(HttpStatus.BAD_GATEWAY, "Couldn't load students. Try again.");
        }

        String code = institutionCode.get();
        List<Map<String, Object>> students = StreamSupport.stream(all.spliterator(), false)
                // Defence in depth: drop anything not belonging to this school.
                .filter(s -> code.equalsIgnoreCase(text(s, "institutionCode")))
                .filter(s -> !"completed".equalsIgnoreCase(text(s, "status")))
                .filter(s -> {
                    String studentClass = text(s, "studentClass");
                    return studentClass != null && wantedClasses.contains(studentClass.trim().toLowerCase(Locale.ROOT));
                })
                .map(this::toCardStudent)
                .sorted(Comparator
                        .comparing((Map<String, Object> s) -> String.valueOf(s.get("studentClass")))
                        .thenComparing(s -> String.valueOf(s.get("lastName"))))
                .toList();

        return ResponseEntity.ok(Map.of("students", students));
    }

    /** Only the fields a card prints - parents, accounts and subjects stay on the server. */
    private Map<String, Object> toCardStudent(JsonNode s) {
        Map<String, Object> student = new LinkedHashMap<>();
        student.put("studentId", text(s, "studentId"));
        student.put("fullName", Stream.of(text(s, "firstName"), text(s, "otherName"), text(s, "lastName"))
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(" ")));
        student.put("lastName", text(s, "lastName"));
        student.put("studentClass", text(s, "studentClass"));
        student.put("dateOfBirth", s.get("dateOfBirth"));
        student.put("dateOfAdmission", s.get("dateOfAdmission"));
        student.put("picture", text(s, "picture"));
        return student;
    }

    private JsonNode post(String path, Object body) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(backendserve + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = HTTP.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.warn("ID cards: {} returned {}", path, response.statusCode());
                return null;
            }
            return response.body() == null || response.body().isBlank() ? null : objectMapper.readTree(response.body());
        } catch (IOException e) {
            log.error("ID cards: call to {} failed", path, e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("ID cards: call to {} interrupted", path);
            return null;
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static ResponseEntity<Object> message(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}

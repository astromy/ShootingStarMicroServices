package com.astromyllc.astroorb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Proxy routes for the new Library module — backs scripts/_libraryCatalogue.js,
 * subscripts/libraryCatalogue.js, scripts/_libraryCirculation.js, and
 * subscripts/libraryCirculation.js on the web side, plus Pulse's Library.js
 * screen on the mobile side.
 *
 * Follows the exact post()/get() proxy pattern used by StoresController,
 * forwarding to the new library microservice at /api/library/**.
 *
 * IMPORTANT — institutionCode: unlike a JWT-derived-claim design, this mirrors
 * how FinanceController's /api/mobile/** aliases actually work in this codebase
 * (getStudentBillDetails / verifyAndRecordPayment etc.) — institutionCode is an
 * explicit field the CALLER must include in the request body, not something
 * this controller infers from the token. Pulse's Library.js did not previously
 * send institutionCode at all; see the accompanying updated Library.js.
 */
@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class LibraryController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gateway.host}")
    private String backendserve;

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private ResponseEntity<String> post(Object body, String url) {
        log.info("Library proxy → {}", url);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (IOException | InterruptedException e) {
            log.error("Library proxy error for {}: {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private ResponseEntity<String> get(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET().build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // WEB — CATALOGUE (admin book management)
    // Used by scripts/_libraryCatalogue.js
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("library/books/create")
    public ResponseEntity<String> createBook(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/books/create");
    }

    @PostMapping("library/books/update/{id}")
    public ResponseEntity<String> updateBook(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/books/update/" + id);
    }

    @PostMapping("library/books/deactivate/{id}")
    public ResponseEntity<String> deactivateBook(@PathVariable String id) {
        return post(Map.of(), backendserve + "/api/library/books/deactivate/" + id);
    }

    @PostMapping("library/books/get-by-institution")
    public ResponseEntity<String> getBooksByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/books/get-by-institution");
    }

    @PostMapping("library/books/search")
    public ResponseEntity<String> searchBooks(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/books/search");
    }

    @GetMapping("library/books/code/{institutionCode}/{bookCode}")
    public ResponseEntity<String> getBookByCodeWeb(
            @PathVariable String institutionCode, @PathVariable String bookCode) {
        return get(backendserve + "/api/library/books/code/" + institutionCode + "/" + bookCode);
    }

    @GetMapping("library/books/{id}")
    public ResponseEntity<String> getBookById(@PathVariable String id) {
        return get(backendserve + "/api/library/books/" + id);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // WEB — CIRCULATION (checkout / return / active / overdue)
    // Used by scripts/_libraryCirculation.js
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("library/loans/checkout")
    public ResponseEntity<String> checkout(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/loans/checkout");
    }

    @PostMapping("library/loans/return")
    public ResponseEntity<String> returnBook(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/loans/return");
    }

    @PostMapping("library/loans/get-by-institution")
    public ResponseEntity<String> getLoansByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/loans/get-by-institution");
    }

    @PostMapping("library/loans/get-by-student")
    public ResponseEntity<String> getLoansByStudent(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/library/loans/get-by-student");
    }

    @GetMapping("library/loans/active/{institutionCode}")
    public ResponseEntity<String> getActiveLoans(@PathVariable String institutionCode) {
        return get(backendserve + "/api/library/loans/active/" + institutionCode);
    }

    @GetMapping("library/loans/overdue/{institutionCode}")
    public ResponseEntity<String> getOverdueLoans(@PathVariable String institutionCode) {
        return get(backendserve + "/api/library/loans/overdue/" + institutionCode);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MOBILE — backs Pulse's Library.js (LIBRARY_LOOKUP_ENDPOINT / LIBRARY_TRANSACTION_ENDPOINT)
    //
    // Matches the FinanceController precedent: these are thin aliases with the
    // SAME request shape as the web routes above, just under /api/mobile/**.
    // institutionCode must be present in the request body — see Library.js update.
    // ══════════════════════════════════════════════════════════════════════════

    /** Body: { institutionCode, code } — code is the scanned/typed bookCode */
    @PostMapping("/api/mobile/getBookByCode")
    public ResponseEntity<String> getBookByCodeMobile(@RequestBody Map<String, Object> body) {
        Object institutionCode = body.get("institutionCode");
        Object code = body.get("code");
        if (institutionCode == null || code == null) {
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"institutionCode and code are both required\"}");
        }
        return get(backendserve + "/api/library/books/code/" + institutionCode + "/" + code);
    }

    /** Body: { institutionCode, studentIndex, bookCode, action: "CHECKOUT" | "RETURN", processedBy } */
    @PostMapping("/api/mobile/libraryTransaction")
    public ResponseEntity<String> libraryTransactionMobile(@RequestBody Map<String, Object> body) {
        Object actionObj = body.get("action");
        String action = actionObj == null ? "" : actionObj.toString().trim().toUpperCase();

        if ("CHECKOUT".equals(action)) {
            return post(body, backendserve + "/api/library/loans/checkout");
        }
        if ("RETURN".equals(action)) {
            return post(body, backendserve + "/api/library/loans/return");
        }
        return ResponseEntity.badRequest()
                .body("{\"error\":\"action must be CHECKOUT or RETURN, got: " + action + "\"}");
    }
}

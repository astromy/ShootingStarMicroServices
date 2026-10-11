package com.astromyllc.shootingstar.hr.controller;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffDetailRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffPageRequest;
import com.astromyllc.shootingstar.hr.dto.response.PagedResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffRowResponse;
import com.astromyllc.shootingstar.hr.dto.response.StaffSummaryResponse;
import com.astromyllc.shootingstar.hr.service.StaffQueryService;
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
 * Light-weight reads for the web screens: counts, one page of staff, one person, one picture.
 * The school code arrives from the ORB proxy, which takes it from the signed-in user.
 */
@RestController
public class StaffQueryController {

    private final StaffQueryService queries;

    public StaffQueryController(StaffQueryService queries) {
        this.queries = queries;
    }

    /** Counts for the dashboards. Body: {"val": "<school code>"} */
    @PostMapping("/api/hr/staff-summary")
    public ResponseEntity<StaffSummaryResponse> staffSummary(@RequestBody SingleStringRequest school) {
        return ResponseEntity.ok(queries.summary(school.getVal()));
    }

    /** One page of staff. No pictures, documents, dependants or records in it. */
    @PostMapping("/api/hr/getStaffPage")
    public ResponseEntity<PagedResponse<StaffRowResponse>> staffPage(@RequestBody StaffPageRequest request) {
        return ResponseEntity.ok(queries.page(request));
    }

    /** The whole record of one staff member, for the edit screen. */
    @PostMapping("/api/hr/getStaffDetail")
    public ResponseEntity<StaffResponse> staffDetail(@RequestBody StaffDetailRequest request) {
        return queries.detail(request.getInstitutionCode(), request.getStaffCode())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Everyone in a school, without pictures or document contents - for drop-downs and name lists. */
    @PostMapping("/api/hr/getStaffByInstitutionWithoutFiles")
    public ResponseEntity<List<StaffResponse>> staffWithoutFiles(@RequestBody SingleStringRequest school) {
        return ResponseEntity.ok(queries.listWithoutFiles(school.getVal()));
    }

    /**
     * A person's picture as an image file. Small version by default; size=full for the original.
     * Browsers keep it (ETag + max-age), so a picture is downloaded once, not on every page load.
     */
    @GetMapping("/api/hr/staff-photo/{institutionCode}/{staffCode}")
    public ResponseEntity<byte[]> staffPhoto(@PathVariable String institutionCode,
                                             @PathVariable String staffCode,
                                             @RequestParam(value = "size", defaultValue = "thumb") String size,
                                             @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {
        Optional<StaffQueryService.PictureResult> found = queries.photo(institutionCode, staffCode, !"full".equalsIgnoreCase(size));
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        StaffQueryService.PictureResult result = found.get();
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

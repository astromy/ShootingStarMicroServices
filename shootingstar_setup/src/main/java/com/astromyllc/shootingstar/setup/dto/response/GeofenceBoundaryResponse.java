package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Restructured from a single flat boundary list to one entry per campus, now
// that an institution can have several named fences instead of exactly one.
// A single-campus institution just gets a list with one entry named
// "Main Campus" - no special-casing needed on the client for that case.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GeofenceBoundaryResponse {
    private List<CampusBoundary> campuses;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class CampusBoundary {
        private String campusName;
        private List<GeoCoordinateResponse> boundary;
    }
}

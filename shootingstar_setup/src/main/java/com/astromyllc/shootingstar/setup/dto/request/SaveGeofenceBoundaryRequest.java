package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SaveGeofenceBoundaryRequest {

    private List<GeoPoint> boundary;
    @NonNull
    private String institution;
    // Which campus this boundary belongs to. Omitted/blank defaults to
    // "Main Campus" (see GeoCoordinateService) so existing single-campus
    // callers don't need to change anything.
    private String campusName;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class GeoPoint {
        private double latitude;
        private double longitude;
    }
}
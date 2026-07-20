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

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class GeoPoint {
        private double latitude;
        private double longitude;
    }
}
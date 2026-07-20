package com.astromyllc.astroorb.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SaveGeofenceBoundaryRequest {

    @NonNull
    private String institution;
    private List<GeoPoint> boundary;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class GeoPoint {
        private double latitude;
        private double longitude;
    }
}
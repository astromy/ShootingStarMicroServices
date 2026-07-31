package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RouteRequest {
    @NonNull
    private String institution;
    private List<RouteDetails> routeDetailsList;
}
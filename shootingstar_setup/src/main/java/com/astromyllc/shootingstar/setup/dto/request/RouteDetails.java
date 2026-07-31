package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RouteDetails {
    private Long idRoute;
    @NonNull
    private String name;
    private String description;
}
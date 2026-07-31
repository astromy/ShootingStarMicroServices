package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RouteResponse {
    private Long idRoute;
    private String name;
    private String description;
}
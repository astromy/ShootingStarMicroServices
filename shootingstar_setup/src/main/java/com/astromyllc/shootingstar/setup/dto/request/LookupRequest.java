package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LookupRequest {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String type;
}

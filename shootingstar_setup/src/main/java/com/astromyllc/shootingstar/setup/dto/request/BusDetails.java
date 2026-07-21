package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusDetails {
    private Long idBus;
    @NonNull
    private String name;
    private String plateNumber;
}
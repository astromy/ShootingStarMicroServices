package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ConsumableRequest {
    private Long idConsumable;
    private String institutionCode;
    private Long idBlock;
    private String name;
    private Integer quantity;
    private String unit;
    private String stockStatus;
    private LocalDate lastRestockedDate;
}

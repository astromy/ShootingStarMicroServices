package com.astromyllc.shootingstar.accommodation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ConsumableResponse {
    private Long idConsumable;
    private Long idBlock;
    private String blockName;
    private String name;
    private Integer quantity;
    private String unit;
    private String stockStatus;
    private LocalDate lastRestockedDate;
}

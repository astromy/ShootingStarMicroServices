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
public class AmenityResponse {
    private Long idAmenity;
    private Long idBlock;
    private String blockName;
    private Long idBlockRoom;
    private String roomName;
    private String name;
    private String condition;
    private String notes;
    private LocalDate lastCheckedDate;
}

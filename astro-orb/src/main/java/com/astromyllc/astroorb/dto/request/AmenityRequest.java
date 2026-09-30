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
public class AmenityRequest {
    private Long idAmenity;
    private String institutionCode;
    private Long idBlock;
    private Long idBlockRoom;
    private String name;
    private String condition;
    private String notes;
    private LocalDate lastCheckedDate;
}

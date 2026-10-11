package com.astromyllc.shootingstar.accommodation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DutyTypeResponse {
    private Long idDutyType;
    private String institutionCode;
    private String name;
    private String description;
}

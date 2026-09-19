package com.astromyllc.shootingstar.clinic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VisitFetchRequest {
    private String institutionCode;

    /**
     * Optional filter — IN_PROGRESS | DISCHARGED. Null/blank = all.
     */
    private String status;
}
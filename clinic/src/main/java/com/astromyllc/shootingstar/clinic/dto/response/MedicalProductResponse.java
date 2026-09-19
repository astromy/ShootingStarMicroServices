package com.astromyllc.shootingstar.clinic.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MedicalProductResponse {
    private Long id;
    private String institutionCode;
    private String productCode;
    private String name;
    private String category;
    private String unit;
    private Integer totalStock;
    private Integer availableStock;
    private Integer reorderLevel;
    private Boolean active;

    /**
     * computed, not stored — same reasoning as Library's BookResponse/LoanResponse "overdue" flag
     */
    private Boolean lowStock;
}
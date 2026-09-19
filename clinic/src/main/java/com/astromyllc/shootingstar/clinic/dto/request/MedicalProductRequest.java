package com.astromyllc.shootingstar.clinic.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MedicalProductRequest {
    private String institutionCode;
    private String productCode;
    private String name;
    private String category;
    private String unit;
    private Integer totalStock;
    private Integer reorderLevel;
}
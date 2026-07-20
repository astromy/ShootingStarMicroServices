package com.astromyllc.shootingstar.storesinventory.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class RestockRequest {
    private String institutionCode;
    private String storeItemId;
    private Integer quantityToAdd;
    private String performedBy;
    private String notes;             // e.g. "PO-2025-001"
}

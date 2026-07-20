package com.astromyllc.shootingstar.storesinventory.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StockMovementResponse {
    private String id;
    private String institutionCode;
    private String storeItemId;
    private String itemName;
    private String movementType;
    private Integer quantityChange;
    private Integer balanceAfter;
    private String referenceId;
    private String performedBy;
    private String notes;
    private LocalDateTime movementDate;
}

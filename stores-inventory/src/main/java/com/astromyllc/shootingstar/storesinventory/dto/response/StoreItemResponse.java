package com.astromyllc.shootingstar.storesinventory.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StoreItemResponse {
    private String id;
    private String institutionCode;
    private String itemName;
    private String itemCode;
    private String itemType;
    private String description;
    private Double unitPrice;
    private Integer quantityInStock;
    private Integer reorderLevel;
    private String channel;
    private Boolean active;
    private Boolean lowStock;         // true if quantityInStock <= reorderLevel
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

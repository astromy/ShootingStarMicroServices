package com.astromyllc.shootingstar.storesinventory.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StoreItemRequest {
    private String institutionCode;
    private String itemName;
    private String itemCode;
    private String itemType;          // PROSPECTUS | UNIFORM | STATIONERY | OTHER
    private String description;
    private Double unitPrice;
    private Integer quantityInStock;
    private Integer reorderLevel;
    private String channel;           // ONLINE | INSTORE | BOTH
}

package com.astromyllc.shootingstar.storesinventory.model;

import lombok.*;

/**
 * One product line within a StoreOrder.
 * Stored embedded, not as a separate collection.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderLineItem {
    private String storeItemId;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private Double unitPrice;
    private Double lineTotal;       // quantity * unitPrice
}

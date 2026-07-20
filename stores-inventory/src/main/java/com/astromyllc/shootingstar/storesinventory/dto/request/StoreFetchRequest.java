package com.astromyllc.shootingstar.storesinventory.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StoreFetchRequest {
    private String institutionCode;
    private String itemType;      // optional filter
    private String channel;       // optional filter: ONLINE | INSTORE | BOTH
    private String buyerId;       // optional: fetch orders for a buyer
    private String status;        // optional: order status filter
    private String orderRef;      // optional: fetch single order
}

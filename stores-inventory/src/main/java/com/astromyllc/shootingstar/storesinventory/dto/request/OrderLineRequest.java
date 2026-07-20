package com.astromyllc.shootingstar.storesinventory.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class OrderLineRequest {
    private String storeItemId;
    private Integer quantity;
}

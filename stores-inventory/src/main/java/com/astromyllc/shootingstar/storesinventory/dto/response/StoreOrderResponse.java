package com.astromyllc.shootingstar.storesinventory.dto.response;

import com.astromyllc.shootingstar.storesinventory.model.OrderLineItem;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StoreOrderResponse {
    private String id;
    private String institutionCode;
    private String orderRef;
    private String buyerId;
    private String buyerName;
    private String buyerContact;
    private String channel;
    private String status;
    private List<OrderLineItem> lineItems;
    private Double totalAmount;
    private String paymentMethod;
    private String paymentReference;
    private Boolean paymentConfirmed;
    private String processedBy;
    private Boolean ledgerPosted;
    private String ledgerReference;
    private LocalDateTime orderDate;
    private LocalDateTime fulfilledDate;
}

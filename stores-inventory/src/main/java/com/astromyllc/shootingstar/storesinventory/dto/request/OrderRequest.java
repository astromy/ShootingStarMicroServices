package com.astromyllc.shootingstar.storesinventory.dto.request;

import lombok.*;

import java.util.List;

/**
 * Submitted by the storefront (online checkout) or
 * POS terminal (in-store sale).
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class OrderRequest {
    private String institutionCode;
    private String buyerId;
    private String buyerName;
    private String buyerContact;
    private String channel;             // ONLINE | INSTORE
    private List<OrderLineRequest> lineItems;
    private String paymentMethod;       // ONLINE | CASH | MOMO | BANK
    private String paymentReference;
    private Boolean paymentConfirmed;
    private String processedBy;         // staff id for in-store sales
}

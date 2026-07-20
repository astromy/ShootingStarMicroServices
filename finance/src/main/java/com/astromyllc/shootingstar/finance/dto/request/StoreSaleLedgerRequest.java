package com.astromyllc.shootingstar.finance.dto.request;

import lombok.*;

/**
 * Payload for POST /api/finance/ledger/store-sale
 * Sent automatically by the stores-inventory service
 * (FinanceLedgerClient) when a StoreOrder is fulfilled.
 *
 * Posts a CREDIT entry to the institution's "Stores & Prospectus Income"
 * INCOME account (auto-provisioned on first use).
 */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StoreSaleLedgerRequest {
    private String institutionCode;
    private String orderRef;
    private String buyerId;
    private String buyerName;
    private Double amount;
    private String paymentMethod;
    private String paymentReference;   // external reference (Paystack, MoMo, etc.)
    private String description;
    private String transactionDate;    // ISO-8601 string
    private String postedBy;
}

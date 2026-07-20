package com.astromyllc.shootingstar.storesinventory.event;

import lombok.*;

/**
 * Payload sent to Finance service POST /api/finance/ledger/store-sale
 * when a StoreOrder is fulfilled.
 *
 * The Finance service creates a LedgerRecord in the STORES_INCOME
 * financial book for this sale.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LedgerPostRequest {
    private String institutionCode;
    private String orderRef;
    private String buyerId;
    private String buyerName;
    private Double amount;
    private String paymentMethod;     // ONLINE | CASH | MOMO | BANK
    private String paymentReference;
    private String description;       // e.g. "Prospectus sale – ORD-GHS-2025-00123"
    private String transactionDate;   // ISO-8601
    private String postedBy;          // staff id
}

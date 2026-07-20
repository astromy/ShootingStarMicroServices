package com.astromyllc.shootingstar.storesinventory.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A single purchase transaction — online or in-store.
 *
 * Lifecycle:
 *   PENDING  → CONFIRMED (payment verified)
 *            → FULFILLED (items handed over / dispatched)
 *            → CANCELLED
 *
 * When status moves to FULFILLED:
 *   - StockMovement records are written for each line item.
 *   - A ledger entry is POSTed to the Finance service.
 *   - ledgerPosted = true and ledgerReference is stored.
 */
@Document(collection = "store_orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class StoreOrder {

    @Id
    private String id;

    @Indexed
    private String institutionCode;

    @Indexed(unique = true)
    private String orderRef;          // e.g. ORD-GHS-2025-00123

    private String buyerId;           // applicantId / studentId
    private String buyerName;
    private String buyerContact;      // phone or email for receipt

    private String channel;           // ONLINE | INSTORE
    private String status;            // PENDING | CONFIRMED | FULFILLED | CANCELLED

    private List<OrderLineItem> lineItems;
    private Double totalAmount;

    private String paymentMethod;     // ONLINE | CASH | MOMO | BANK
    private String paymentReference;  // Paystack ref, MoMo transaction id, etc.
    private Boolean paymentConfirmed;

    /** Staff member who processed an in-store sale */
    private String processedBy;

    /** Set to true once Finance ledger entry is successfully created */
    private Boolean ledgerPosted;

    /** ID / reference returned by Finance service for the ledger record */
    private String ledgerReference;

    private LocalDateTime orderDate;
    private LocalDateTime fulfilledDate;
}

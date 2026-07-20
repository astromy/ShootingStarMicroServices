package com.astromyllc.shootingstar.storesinventory.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Immutable audit trail for every stock quantity change.
 *
 * movementType:
 *   PURCHASE_SALE  – items sold (negative quantityChange)
 *   RESTOCK        – new stock received (positive)
 *   ADJUSTMENT     – manual stock correction
 *   RETURN         – customer return (positive)
 */
@Document(collection = "stock_movements")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class StockMovement {

    @Id
    private String id;

    @Indexed
    private String institutionCode;

    @Indexed
    private String storeItemId;

    private String itemName;
    private String movementType;     // PURCHASE_SALE | RESTOCK | ADJUSTMENT | RETURN

    /** Negative for sales/write-offs, positive for restocks/returns */
    private Integer quantityChange;

    private Integer balanceAfter;    // snapshot of stock after this movement
    private String referenceId;      // orderId or PO number driving the change
    private String performedBy;
    private String notes;
    private LocalDateTime movementDate;
}

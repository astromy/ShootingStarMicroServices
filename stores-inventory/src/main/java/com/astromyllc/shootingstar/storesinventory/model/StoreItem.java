package com.astromyllc.shootingstar.storesinventory.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * A purchasable item held in the institution's inventory.
 *
 * itemType : PROSPECTUS | UNIFORM | STATIONERY | OTHER
 * channel  : ONLINE | INSTORE | BOTH
 *
 * When quantityInStock falls below reorderLevel the service
 * raises a low-stock alert.
 */
@Document(collection = "store_items")
@CompoundIndexes({
    @CompoundIndex(name = "inst_code_idx", def = "{'institutionCode': 1, 'itemCode': 1}", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class StoreItem {

    @Id
    private String id;

    @Indexed
    private String institutionCode;

    private String itemName;
    private String itemCode;          // human-friendly SKU per institution
    private String itemType;          // PROSPECTUS | UNIFORM | STATIONERY | OTHER
    private String description;
    private Double unitPrice;
    private Integer quantityInStock;
    private Integer reorderLevel;     // trigger threshold for low-stock alert
    private String channel;           // ONLINE | INSTORE | BOTH

    /** false = soft-deleted, hidden from storefront */
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.astromyllc.shootingstar.storesinventory.service;

import com.astromyllc.shootingstar.storesinventory.dto.request.OrderRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreFetchRequest;
import com.astromyllc.shootingstar.storesinventory.dto.response.StoreOrderResponse;

import java.util.List;
import java.util.Optional;

public interface StoreOrderService {

    /** Place a new order (PENDING status) */
    StoreOrderResponse placeOrder(OrderRequest request);

    /** Confirm payment and move to CONFIRMED */
    StoreOrderResponse confirmOrder(String orderId, String paymentReference);

    /**
     * Fulfil a CONFIRMED order:
     *  1. Deducts stock for each line item.
     *  2. Writes StockMovement records.
     *  3. Posts ledger entry to Finance service.
     *  4. Marks ledgerPosted = true.
     */
    StoreOrderResponse fulfilOrder(String orderId, String processedBy);

    StoreOrderResponse cancelOrder(String orderId, String reason);

    Optional<StoreOrderResponse> getOrderByRef(String orderRef);

    List<StoreOrderResponse> getOrdersByInstitution(StoreFetchRequest request);

    List<StoreOrderResponse> getOrdersByBuyer(String institutionCode, String buyerId);

    /** Retry ledger posting for any fulfilled orders where ledgerPosted=false */
    void retryFailedLedgerPosts(String institutionCode);
}

package com.astromyllc.shootingstar.storesinventory.controller;

import com.astromyllc.shootingstar.storesinventory.dto.request.OrderRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreFetchRequest;
import com.astromyllc.shootingstar.storesinventory.dto.response.StoreOrderResponse;
import com.astromyllc.shootingstar.storesinventory.service.StoreOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Slf4j
public class StoreOrderController {

    private final StoreOrderService storeOrderService;

    /**
     * Applicant / student places an order — works for both ONLINE and INSTORE.
     * In-store cash sales are immediately CONFIRMED.
     */
    @PostMapping("/orders/place")
    @ResponseStatus(HttpStatus.CREATED)
    public StoreOrderResponse placeOrder(@RequestBody OrderRequest request) {
        log.info("Order placed by {} via {} for {}", request.getBuyerId(),
                request.getChannel(), request.getInstitutionCode());
        return storeOrderService.placeOrder(request);
    }

    /**
     * Called by Paystack webhook or manual staff confirmation.
     * Moves order from PENDING → CONFIRMED.
     */
    @PostMapping("/orders/{orderId}/confirm")
    public StoreOrderResponse confirmOrder(
            @PathVariable String orderId,
            @RequestParam String paymentReference) {
        return storeOrderService.confirmOrder(orderId, paymentReference);
    }

    /**
     * Staff hands over items (in-store) or marks as dispatched (online).
     * Deducts stock and posts ledger entry to Finance.
     */
    @PostMapping("/orders/{orderId}/fulfil")
    public StoreOrderResponse fulfilOrder(
            @PathVariable String orderId,
            @RequestParam String processedBy) {
        log.info("Fulfilling order {} by {}", orderId, processedBy);
        return storeOrderService.fulfilOrder(orderId, processedBy);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public StoreOrderResponse cancelOrder(
            @PathVariable String orderId,
            @RequestParam(required = false) String reason) {
        return storeOrderService.cancelOrder(orderId, reason);
    }

    @GetMapping("/orders/ref/{orderRef}")
    public Optional<StoreOrderResponse> getByRef(@PathVariable String orderRef) {
        return storeOrderService.getOrderByRef(orderRef);
    }

    /** All orders for an institution (optional status/channel filter) */
    @PostMapping("/orders/get-by-institution")
    public List<StoreOrderResponse> getByInstitution(@RequestBody StoreFetchRequest request) {
        return storeOrderService.getOrdersByInstitution(request);
    }

    /** All orders placed by a specific buyer (applicant self-service) */
    @GetMapping("/orders/buyer/{institutionCode}/{buyerId}")
    public List<StoreOrderResponse> getByBuyer(
            @PathVariable String institutionCode,
            @PathVariable String buyerId) {
        return storeOrderService.getOrdersByBuyer(institutionCode, buyerId);
    }

    /** Admin/cron: retry ledger posts for fulfilled orders that failed to post */
    @PostMapping("/orders/retry-ledger/{institutionCode}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void retryLedger(@PathVariable String institutionCode) {
        storeOrderService.retryFailedLedgerPosts(institutionCode);
    }
}

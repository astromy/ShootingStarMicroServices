package com.astromyllc.astroorb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Proxy routes for the Stores &amp; Inventory module — backs
 * scripts/_storesInventory.js and subscripts/storesInventory.js.
 *
 * Follows the exact post()/get() proxy pattern used by FinanceController,
 * forwarding to the stores-inventory microservice at /api/stores/**.
 */
@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class StoresController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${gateway.host}")
    private String backendserve;

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private ResponseEntity<String> post(Object body, String url) {
        log.info("Stores proxy → {}", url);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (IOException | InterruptedException e) {
            log.error("Stores proxy error for {}: {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private ResponseEntity<String> get(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET().build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ITEMS / CATALOGUE
    // Used by window.storesLoad / storesFetchOnlineItems / storesFetchInstoreItems
    // / storesFetchLowStock / storesFetchAllItems / storesCreateItem / storesRestock
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * JS calls fetchPost('stores/items/online/' + inst, {}) — POST with empty body
     * proxied to a GET on the backend.
     */
    @PostMapping("stores/items/online/{institutionCode}")
    public ResponseEntity<String> getOnlineItems(@PathVariable String institutionCode) {
        return get(backendserve + "/api/stores/items/online/" + institutionCode);
    }

    @PostMapping("stores/items/instore/{institutionCode}")
    public ResponseEntity<String> getInstoreItems(@PathVariable String institutionCode) {
        return get(backendserve + "/api/stores/items/instore/" + institutionCode);
    }

    @PostMapping("stores/items/low-stock/{institutionCode}")
    public ResponseEntity<String> getLowStock(@PathVariable String institutionCode) {
        return get(backendserve + "/api/stores/items/low-stock/" + institutionCode);
    }

    @PostMapping("stores/items/get-by-institution")
    public ResponseEntity<String> getItemsByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/stores/items/get-by-institution");
    }

    @PostMapping("stores/items/create")
    public ResponseEntity<String> createItem(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/stores/items/create");
    }

    @PostMapping("stores/items/update/{id}")
    public ResponseEntity<String> updateItem(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/stores/items/update/" + id);
    }

    @PostMapping("stores/items/deactivate/{id}")
    public ResponseEntity<String> deactivateItem(@PathVariable String id) {
        return post(Map.of(), backendserve + "/api/stores/items/deactivate/" + id);
    }

    @PostMapping("stores/items/restock")
    public ResponseEntity<String> restock(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/stores/items/restock");
    }

    @GetMapping("stores/items/{id}")
    public ResponseEntity<String> getItemById(@PathVariable String id) {
        return get(backendserve + "/api/stores/items/" + id);
    }

    /** Full stock movement audit trail — window.storesFetchMovements */
    @PostMapping("stores/movements/{institutionCode}")
    public ResponseEntity<String> getMovements(@PathVariable String institutionCode) {
        return get(backendserve + "/api/stores/movements/" + institutionCode);
    }

    /** Stock movement history for a single item — window.storesFetchMovementsByItem */
    @PostMapping("stores/movements/item/{storeItemId}")
    public ResponseEntity<String> getMovementsByItem(@PathVariable String storeItemId) {
        return get(backendserve + "/api/stores/movements/item/" + storeItemId);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ORDERS
    // Used by window.storesPlaceOrder / storesConfirmOrder / storesFulfilOrder /
    // storesCancelOrder / storesFetchOrdersByInstitution / storesFetchOrdersByBuyer
    // / storesFetchOrderByRef
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("stores/orders/place")
    public ResponseEntity<String> placeOrder(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/stores/orders/place");
    }

    /**
     * JS calls fetchPost('stores/orders/{id}/confirm?paymentReference=...', {})
     */
    @PostMapping("stores/orders/{orderId}/confirm")
    public ResponseEntity<String> confirmOrder(
            @PathVariable String orderId,
            @RequestParam String paymentReference,
            @RequestBody(required = false) Map<String, Object> body) {
        return post(Map.of(), backendserve + "/api/stores/orders/" + orderId
                + "/confirm?paymentReference=" + paymentReference);
    }

    @PostMapping("stores/orders/{orderId}/fulfil")
    public ResponseEntity<String> fulfilOrder(
            @PathVariable String orderId,
            @RequestParam String processedBy,
            @RequestBody(required = false) Map<String, Object> body) {
        return post(Map.of(), backendserve + "/api/stores/orders/" + orderId
                + "/fulfil?processedBy=" + processedBy);
    }

    @PostMapping("stores/orders/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable String orderId,
            @RequestParam(required = false) String reason,
            @RequestBody(required = false) Map<String, Object> body) {
        String url = backendserve + "/api/stores/orders/" + orderId + "/cancel"
                + (reason != null ? "?reason=" + reason : "");
        return post(Map.of(), url);
    }

    @PostMapping("stores/orders/ref/{orderRef}")
    public ResponseEntity<String> getByRef(@PathVariable String orderRef) {
        return get(backendserve + "/api/stores/orders/ref/" + orderRef);
    }

    @PostMapping("stores/orders/get-by-institution")
    public ResponseEntity<String> getOrdersByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/stores/orders/get-by-institution");
    }

    @PostMapping("stores/orders/buyer/{institutionCode}/{buyerId}")
    public ResponseEntity<String> getOrdersByBuyer(
            @PathVariable String institutionCode,
            @PathVariable String buyerId) {
        return get(backendserve + "/api/stores/orders/buyer/" + institutionCode + "/" + buyerId);
    }

    @PostMapping("stores/orders/retry-ledger/{institutionCode}")
    public ResponseEntity<String> retryLedger(@PathVariable String institutionCode) {
        return post(Map.of(), backendserve + "/api/stores/orders/retry-ledger/" + institutionCode);
    }
}

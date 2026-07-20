package com.astromyllc.shootingstar.storesinventory.controller;

import com.astromyllc.shootingstar.storesinventory.dto.request.RestockRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreItemRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreFetchRequest;
import com.astromyllc.shootingstar.storesinventory.dto.response.StoreItemResponse;
import com.astromyllc.shootingstar.storesinventory.service.StoreItemService;
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
public class StoreItemController {

    private final StoreItemService storeItemService;

    /** Admin: create a new inventory item */
    @PostMapping("/items/create")
    @ResponseStatus(HttpStatus.CREATED)
    public StoreItemResponse createItem(@RequestBody StoreItemRequest request) {
        log.info("Create item: {} for {}", request.getItemName(), request.getInstitutionCode());
        return storeItemService.createItem(request);
    }

    /** Admin: update item details */
    @PutMapping("/items/{id}")
    public StoreItemResponse updateItem(@PathVariable String id, @RequestBody StoreItemRequest request) {
        return storeItemService.updateItem(id, request);
    }

    /** Admin: update item details — POST alias for fetchPost-based frontends */
    @PostMapping("/items/update/{id}")
    public StoreItemResponse updateItemPost(@PathVariable String id, @RequestBody StoreItemRequest request) {
        return storeItemService.updateItem(id, request);
    }

    /** Admin: soft-delete an item */
    @DeleteMapping("/items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateItem(@PathVariable String id) {
        storeItemService.deactivateItem(id);
    }

    /** Admin: soft-delete an item — POST alias for fetchPost-based frontends */
    @PostMapping("/items/deactivate/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateItemPost(@PathVariable String id) {
        storeItemService.deactivateItem(id);
    }

    /** All active items for an institution (admin view) */
    @PostMapping("/items/get-by-institution")
    public List<StoreItemResponse> getByInstitution(@RequestBody StoreFetchRequest request) {
        return storeItemService.getItemsByInstitution(request);
    }

    /** Storefront: items available ONLINE */
    @GetMapping("/items/online/{institutionCode}")
    public List<StoreItemResponse> getOnlineItems(@PathVariable String institutionCode) {
        return storeItemService.getOnlineItems(institutionCode);
    }

    /** POS: items available IN-STORE */
    @GetMapping("/items/instore/{institutionCode}")
    public List<StoreItemResponse> getInstoreItems(@PathVariable String institutionCode) {
        return storeItemService.getInstoreItems(institutionCode);
    }

    /** Dashboard: low-stock alert list */
    @GetMapping("/items/low-stock/{institutionCode}")
    public List<StoreItemResponse> getLowStock(@PathVariable String institutionCode) {
        return storeItemService.getLowStockItems(institutionCode);
    }

    /** Admin: restock an item */
    @PostMapping("/items/restock")
    public StoreItemResponse restock(@RequestBody RestockRequest request) {
        log.info("Restock: {} +{}", request.getStoreItemId(), request.getQuantityToAdd());
        return storeItemService.restock(request);
    }

    @GetMapping("/items/{id}")
    public Optional<StoreItemResponse> getById(@PathVariable String id) {
        return storeItemService.getItemById(id);
    }

    /** Full stock movement audit trail for an institution (newest first) */
    @GetMapping("/movements/{institutionCode}")
    public List<com.astromyllc.shootingstar.storesinventory.dto.response.StockMovementResponse> getMovements(
            @PathVariable String institutionCode) {
        return storeItemService.getStockMovements(institutionCode);
    }

    /** Stock movement audit trail for a single item (newest first) */
    @GetMapping("/movements/item/{storeItemId}")
    public List<com.astromyllc.shootingstar.storesinventory.dto.response.StockMovementResponse> getMovementsByItem(
            @PathVariable String storeItemId) {
        return storeItemService.getStockMovementsByItem(storeItemId);
    }
}

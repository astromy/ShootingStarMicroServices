package com.astromyllc.shootingstar.storesinventory.service;

import com.astromyllc.shootingstar.storesinventory.dto.request.RestockRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreItemRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreFetchRequest;
import com.astromyllc.shootingstar.storesinventory.dto.response.StoreItemResponse;

import java.util.List;
import java.util.Optional;

public interface StoreItemService {

    StoreItemResponse createItem(StoreItemRequest request);

    StoreItemResponse updateItem(String id, StoreItemRequest request);

    Optional<StoreItemResponse> getItemById(String id);

    List<StoreItemResponse> getItemsByInstitution(StoreFetchRequest request);

    /** Items available on the online storefront for the given institution */
    List<StoreItemResponse> getOnlineItems(String institutionCode);

    /** Items available in-store */
    List<StoreItemResponse> getInstoreItems(String institutionCode);

    /** Items at or below reorder level */
    List<StoreItemResponse> getLowStockItems(String institutionCode);

    /** Full stock movement audit trail for an institution, newest first */
    List<com.astromyllc.shootingstar.storesinventory.dto.response.StockMovementResponse>
            getStockMovements(String institutionCode);

    /** Stock movement audit trail for a single item, newest first */
    List<com.astromyllc.shootingstar.storesinventory.dto.response.StockMovementResponse>
            getStockMovementsByItem(String storeItemId);

    StoreItemResponse restock(RestockRequest request);

    void deactivateItem(String id);
}

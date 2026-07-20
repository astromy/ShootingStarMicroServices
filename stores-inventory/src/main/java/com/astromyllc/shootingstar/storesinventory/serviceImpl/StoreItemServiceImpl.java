package com.astromyllc.shootingstar.storesinventory.serviceImpl;

import com.astromyllc.shootingstar.storesinventory.dto.request.RestockRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreItemRequest;
import com.astromyllc.shootingstar.storesinventory.dto.request.StoreFetchRequest;
import com.astromyllc.shootingstar.storesinventory.dto.response.StoreItemResponse;
import com.astromyllc.shootingstar.storesinventory.dto.response.StockMovementResponse;
import com.astromyllc.shootingstar.storesinventory.model.StockMovement;
import com.astromyllc.shootingstar.storesinventory.model.StoreItem;
import com.astromyllc.shootingstar.storesinventory.repository.StockMovementRepository;
import com.astromyllc.shootingstar.storesinventory.repository.StoreItemRepository;
import com.astromyllc.shootingstar.storesinventory.service.StoreItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreItemServiceImpl implements StoreItemService {

    private final StoreItemRepository storeItemRepository;
    private final StockMovementRepository stockMovementRepository;

    @Override
    public StoreItemResponse createItem(StoreItemRequest req) {
        StoreItem item = StoreItem.builder()
                .institutionCode(req.getInstitutionCode())
                .itemName(req.getItemName())
                .itemCode(req.getItemCode())
                .itemType(req.getItemType())
                .description(req.getDescription())
                .unitPrice(req.getUnitPrice())
                .quantityInStock(req.getQuantityInStock() != null ? req.getQuantityInStock() : 0)
                .reorderLevel(req.getReorderLevel() != null ? req.getReorderLevel() : 5)
                .channel(req.getChannel())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        StoreItem saved = storeItemRepository.save(item);

        // Record initial stock as a RESTOCK movement
        if (saved.getQuantityInStock() > 0) {
            stockMovementRepository.save(StockMovement.builder()
                    .institutionCode(saved.getInstitutionCode())
                    .storeItemId(saved.getId())
                    .itemName(saved.getItemName())
                    .movementType("RESTOCK")
                    .quantityChange(saved.getQuantityInStock())
                    .balanceAfter(saved.getQuantityInStock())
                    .performedBy("SYSTEM")
                    .notes("Initial stock entry")
                    .movementDate(LocalDateTime.now())
                    .build());
        }
        return toResponse(saved);
    }

    @Override
    public StoreItemResponse updateItem(String id, StoreItemRequest req) {
        StoreItem item = storeItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));
        item.setItemName(req.getItemName());
        item.setDescription(req.getDescription());
        item.setUnitPrice(req.getUnitPrice());
        item.setReorderLevel(req.getReorderLevel());
        item.setChannel(req.getChannel());
        item.setItemType(req.getItemType());
        item.setUpdatedAt(LocalDateTime.now());
        return toResponse(storeItemRepository.save(item));
    }

    @Override
    public Optional<StoreItemResponse> getItemById(String id) {
        return storeItemRepository.findById(id).map(this::toResponse);
    }

    @Override
    public List<StoreItemResponse> getItemsByInstitution(StoreFetchRequest req) {
        List<StoreItem> items;
        if (req.getItemType() != null) {
            items = storeItemRepository.findByInstitutionCodeAndItemTypeAndActiveTrue(
                    req.getInstitutionCode(), req.getItemType());
        } else {
            items = storeItemRepository.findByInstitutionCodeAndActiveTrue(req.getInstitutionCode());
        }
        return items.stream().map(this::toResponse).toList();
    }

    @Override
    public List<StoreItemResponse> getOnlineItems(String institutionCode) {
        return storeItemRepository.findByInstitutionCodeAndChannelInAndActiveTrue(
                        institutionCode, List.of("ONLINE", "BOTH"))
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<StoreItemResponse> getInstoreItems(String institutionCode) {
        return storeItemRepository.findByInstitutionCodeAndChannelInAndActiveTrue(
                        institutionCode, List.of("INSTORE", "BOTH"))
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<StoreItemResponse> getLowStockItems(String institutionCode) {
        // We compare against each item's own reorderLevel dynamically
        return storeItemRepository.findByInstitutionCodeAndActiveTrue(institutionCode)
                .stream()
                .filter(i -> i.getQuantityInStock() <= i.getReorderLevel())
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<StockMovementResponse> getStockMovements(String institutionCode) {
        return stockMovementRepository.findByInstitutionCodeOrderByMovementDateDesc(institutionCode)
                .stream().map(this::toMovementResponse).toList();
    }

    @Override
    public List<StockMovementResponse> getStockMovementsByItem(String storeItemId) {
        return stockMovementRepository.findByStoreItemIdOrderByMovementDateDesc(storeItemId)
                .stream().map(this::toMovementResponse).toList();
    }

    @Override
    public StoreItemResponse restock(RestockRequest req) {
        StoreItem item = storeItemRepository.findById(req.getStoreItemId())
                .orElseThrow(() -> new RuntimeException("Item not found: " + req.getStoreItemId()));

        int previous = item.getQuantityInStock();
        int newQty = previous + req.getQuantityToAdd();
        item.setQuantityInStock(newQty);
        item.setUpdatedAt(LocalDateTime.now());
        StoreItem saved = storeItemRepository.save(item);

        stockMovementRepository.save(StockMovement.builder()
                .institutionCode(saved.getInstitutionCode())
                .storeItemId(saved.getId())
                .itemName(saved.getItemName())
                .movementType("RESTOCK")
                .quantityChange(req.getQuantityToAdd())
                .balanceAfter(newQty)
                .performedBy(req.getPerformedBy())
                .notes(req.getNotes())
                .movementDate(LocalDateTime.now())
                .build());

        log.info("Restocked {} (+{}) → new stock: {}", saved.getItemName(), req.getQuantityToAdd(), newQty);
        return toResponse(saved);
    }

    @Override
    public void deactivateItem(String id) {
        StoreItem item = storeItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found: " + id));
        item.setActive(false);
        item.setUpdatedAt(LocalDateTime.now());
        storeItemRepository.save(item);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private StockMovementResponse toMovementResponse(StockMovement m) {
        return StockMovementResponse.builder()
                .id(m.getId())
                .institutionCode(m.getInstitutionCode())
                .storeItemId(m.getStoreItemId())
                .itemName(m.getItemName())
                .movementType(m.getMovementType())
                .quantityChange(m.getQuantityChange())
                .balanceAfter(m.getBalanceAfter())
                .referenceId(m.getReferenceId())
                .performedBy(m.getPerformedBy())
                .notes(m.getNotes())
                .movementDate(m.getMovementDate())
                .build();
    }

    private StoreItemResponse toResponse(StoreItem i) {
        return StoreItemResponse.builder()
                .id(i.getId())
                .institutionCode(i.getInstitutionCode())
                .itemName(i.getItemName())
                .itemCode(i.getItemCode())
                .itemType(i.getItemType())
                .description(i.getDescription())
                .unitPrice(i.getUnitPrice())
                .quantityInStock(i.getQuantityInStock())
                .reorderLevel(i.getReorderLevel())
                .channel(i.getChannel())
                .active(i.getActive())
                .lowStock(i.getQuantityInStock() <= i.getReorderLevel())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}

package com.astromyllc.shootingstar.storesinventory.repository;

import com.astromyllc.shootingstar.storesinventory.model.StoreItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StoreItemRepository extends MongoRepository<StoreItem, String> {

    List<StoreItem> findByInstitutionCodeAndActiveTrue(String institutionCode);

    List<StoreItem> findByInstitutionCodeAndItemTypeAndActiveTrue(String institutionCode, String itemType);

    Optional<StoreItem> findByInstitutionCodeAndItemCode(String institutionCode, String itemCode);

    List<StoreItem> findByInstitutionCodeAndChannelInAndActiveTrue(String institutionCode, List<String> channels);

    /** Items at or below reorder level — used for low-stock alerts */
    List<StoreItem> findByInstitutionCodeAndActiveTrueAndQuantityInStockLessThanEqual(
            String institutionCode, int reorderLevel);
}

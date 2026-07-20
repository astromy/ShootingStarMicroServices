package com.astromyllc.shootingstar.storesinventory.repository;

import com.astromyllc.shootingstar.storesinventory.model.StockMovement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StockMovementRepository extends MongoRepository<StockMovement, String> {

    List<StockMovement> findByInstitutionCodeOrderByMovementDateDesc(String institutionCode);

    List<StockMovement> findByStoreItemIdOrderByMovementDateDesc(String storeItemId);

    List<StockMovement> findByInstitutionCodeAndMovementType(String institutionCode, String movementType);
}

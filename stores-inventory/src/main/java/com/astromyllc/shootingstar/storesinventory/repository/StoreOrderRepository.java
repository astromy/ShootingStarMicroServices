package com.astromyllc.shootingstar.storesinventory.repository;

import com.astromyllc.shootingstar.storesinventory.model.StoreOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StoreOrderRepository extends MongoRepository<StoreOrder, String> {

    Optional<StoreOrder> findByOrderRef(String orderRef);

    List<StoreOrder> findByInstitutionCode(String institutionCode);

    List<StoreOrder> findByInstitutionCodeAndStatus(String institutionCode, String status);

    List<StoreOrder> findByInstitutionCodeAndBuyerId(String institutionCode, String buyerId);

    List<StoreOrder> findByInstitutionCodeAndChannel(String institutionCode, String channel);

    /** Orders not yet posted to finance ledger */
    List<StoreOrder> findByInstitutionCodeAndLedgerPostedFalseAndStatus(
            String institutionCode, String status);
}

package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductRequest;
import com.astromyllc.shootingstar.clinic.dto.response.MedicalProductResponse;
import com.astromyllc.shootingstar.clinic.model.MedicalProduct;
import com.astromyllc.shootingstar.clinic.repository.MedicalProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MedicalProductUtil {
    private final MedicalProductRepository medicalProductRepository;
    public static List<MedicalProduct> medicalProductGlobalList;

    @PostConstruct
    private void fetchAllMedicalProducts() {
        medicalProductGlobalList = medicalProductRepository.findAll();
        log.info("Global Medical Product List populated with {} records", medicalProductGlobalList.size());
    }

    public MedicalProduct mapRequest_ToMedicalProduct(MedicalProductRequest request) {
        int initialStock = request.getTotalStock() == null ? 0 : request.getTotalStock();
        return MedicalProduct.builder()
                .institutionCode(request.getInstitutionCode())
                .productCode(request.getProductCode())
                .name(request.getName())
                .category(request.getCategory())
                .unit(request.getUnit())
                .totalStock(initialStock)
                .availableStock(initialStock)
                .reorderLevel(request.getReorderLevel())
                .active(true)
                .build();
    }

    public MedicalProductResponse mapMedicalProduct_ToMedicalProductResponse(MedicalProduct product) {
        boolean lowStock = product.getReorderLevel() != null
                && product.getAvailableStock() != null
                && product.getAvailableStock() <= product.getReorderLevel();

        return MedicalProductResponse.builder()
                .id(product.getId())
                .institutionCode(product.getInstitutionCode())
                .productCode(product.getProductCode())
                .name(product.getName())
                .category(product.getCategory())
                .unit(product.getUnit())
                .totalStock(product.getTotalStock())
                .availableStock(product.getAvailableStock())
                .reorderLevel(product.getReorderLevel())
                .active(product.getActive())
                .lowStock(lowStock)
                .build();
    }
}

package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductRequest;
import com.astromyllc.shootingstar.clinic.dto.response.MedicalProductResponse;
import com.astromyllc.shootingstar.clinic.model.MedicalProduct;
import com.astromyllc.shootingstar.clinic.repository.MedicalProductRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.MedicalProductServiceInterface;
import com.astromyllc.shootingstar.clinic.util.MedicalProductUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedicalProductService implements MedicalProductServiceInterface {

    private final MedicalProductRepository medicalProductRepository;
    private final MedicalProductUtil medicalProductUtil;

    @Override
    public MedicalProductResponse createProduct(MedicalProductRequest request) {
        MedicalProduct product = medicalProductUtil.mapRequest_ToMedicalProduct(request);
        product = medicalProductRepository.save(product);
        MedicalProductUtil.medicalProductGlobalList.add(product);
        log.info("Medical product created: {} ({}) at institution {}",
                product.getName(), product.getProductCode(), product.getInstitutionCode());
        return medicalProductUtil.mapMedicalProduct_ToMedicalProductResponse(product);
    }

    @Override
    public List<MedicalProductResponse> fetchProductsByInstitution(MedicalProductFetchRequest request) {
        return MedicalProductUtil.medicalProductGlobalList.stream()
                .filter(p -> p.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())
                        && Boolean.TRUE.equals(p.getActive()))
                .map(medicalProductUtil::mapMedicalProduct_ToMedicalProductResponse)
                .toList();
    }

    @Override
    public List<MedicalProductResponse> fetchLowStockProducts(MedicalProductFetchRequest request) {
        return MedicalProductUtil.medicalProductGlobalList.stream()
                .filter(p -> p.getInstitutionCode().equalsIgnoreCase(request.getInstitutionCode())
                        && Boolean.TRUE.equals(p.getActive())
                        && p.getReorderLevel() != null
                        && p.getAvailableStock() != null
                        && p.getAvailableStock() <= p.getReorderLevel())
                .map(medicalProductUtil::mapMedicalProduct_ToMedicalProductResponse)
                .toList();
    }

    @Override
    public MedicalProductResponse deactivateProduct(Long productId) {
        MedicalProduct product = medicalProductRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("No medical product found with id " + productId));
        product.setActive(false);
        product = medicalProductRepository.save(product);
        log.info("Medical product {} deactivated", productId);
        return medicalProductUtil.mapMedicalProduct_ToMedicalProductResponse(product);
    }
}

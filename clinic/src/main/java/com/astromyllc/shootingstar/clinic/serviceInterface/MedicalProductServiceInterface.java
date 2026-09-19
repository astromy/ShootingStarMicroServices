package com.astromyllc.shootingstar.clinic.serviceInterface;

import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductFetchRequest;
import com.astromyllc.shootingstar.clinic.dto.request.MedicalProductRequest;
import com.astromyllc.shootingstar.clinic.dto.response.MedicalProductResponse;

import java.util.List;

public interface MedicalProductServiceInterface {

    MedicalProductResponse createProduct(MedicalProductRequest request);

    List<MedicalProductResponse> fetchProductsByInstitution(MedicalProductFetchRequest request);

    List<MedicalProductResponse> fetchLowStockProducts(MedicalProductFetchRequest request);

    MedicalProductResponse deactivateProduct(Long productId);
}

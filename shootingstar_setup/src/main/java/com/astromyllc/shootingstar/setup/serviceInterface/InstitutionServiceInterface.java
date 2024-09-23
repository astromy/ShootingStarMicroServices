package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;

import java.util.List;
import java.util.Optional;

public interface InstitutionServiceInterface {
    public InstitutionResponse createInstitution(InstitutionRequest institutionRequest);
    public String createPreOrderInstitution(PreOrderInstitutionRequest institutionRequest);
    public Optional<InstitutionResponse> getInstitutionByBeceCode(SingleStringRequest beceCode);
    public Optional<List<InstitutionResponse>> getAllInstitution();
    public Optional<List<InstitutionResponse>> getAllInstitutionByPopulation(int population);
    public Optional<List<InstitutionResponse>> getAllInstitutionByCountry(String country);
    public Optional<List<InstitutionResponse>> getAllInstitutionByStreams(int stream);
    public Optional<List<InstitutionResponse>> getAllInstitutionByCity(String city);
    public Optional<List<InstitutionResponse>> getAllInstitutionByRegion(String region);
    public  Optional<List<InstitutionResponse>> getAllInstitutionByPackage(String subscription);
    public InstitutionResponse migratePreOrder(String institutionCode);

    Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution();
}

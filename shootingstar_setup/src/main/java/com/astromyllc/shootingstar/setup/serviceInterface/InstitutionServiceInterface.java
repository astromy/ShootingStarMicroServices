package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;
import java.util.Optional;

public interface InstitutionServiceInterface {
    public void createInstitution(InstitutionRequest institutionRequest);
    public Optional<InstitutionResponse> getInstitutionByBeceCode(String beceCode);
    public Optional<List<InstitutionResponse>> getAllInstitution();
    public Optional<List<InstitutionResponse>> getAllInstitutionByPopulation(int population);
    public Optional<List<InstitutionResponse>> getAllInstitutionByCountry(String country);
    public Optional<List<InstitutionResponse>> getAllInstitutionByStreams(int stream);
    public Optional<List<InstitutionResponse>> getAllInstitutionByCity(String city);
    public Optional<List<InstitutionResponse>> getAllInstitutionByRegion(String region);
    public  Optional<List<InstitutionResponse>> getAllInstitutionByPackage(String subscription);
}

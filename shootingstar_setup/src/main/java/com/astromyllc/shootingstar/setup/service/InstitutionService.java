package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.InstitutionServiceInterface;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InstitutionService implements InstitutionServiceInterface {
    private final InstitutionRepository institutionRepository;
private final InstitutionUtils institutionUtils;
    @Override
    public void createInstitution(InstitutionRequest institutionRequest) {
        Optional <Institution> institution = (institutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode() == institutionRequest.getBececode()).findFirst());
        if (institution.isEmpty()) {
            Institution institution1 = institutionUtils.mapInstitutionRequest_ToInstitution(institutionRequest);
            institutionRepository.save(institution1);

            institutionUtils.institutionGlobalList.add(institution1);
            log.info("Institution {} Saved Successfully", institution1.getIdInstitution());
        } else {
            institutionRepository.save(institutionUtils.mapInstitutionRequestToInstitution(institution.get(), institutionRequest));
        }
    }

    @Override
    public Optional<InstitutionResponse> getInstitutionByBeceCode(String beceCode) {
        String finalBeceCode= beceCode.split("\"")[3];
        List<Institution> ii=institutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equals(finalBeceCode)).toList();
         Optional<Institution> i=Optional.ofNullable(institutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equals(finalBeceCode)).findFirst().get());
        return Optional.ofNullable(institutionUtils.mapInstitutionToInstitutionResponse(i.get()));
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        return Optional.of(institutionUtils.institutionGlobalList.stream().map(inst -> {
            return institutionUtils.mapInstitutionToInstitutionResponse(inst);
        }).toList());
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByPopulation(int population) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByCountry(String country) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByStreams(int stream) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByCity(String city) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByRegion(String region) {
        return Optional.empty();
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitutionByPackage(String subscription) {
        return Optional.empty();
    }
}

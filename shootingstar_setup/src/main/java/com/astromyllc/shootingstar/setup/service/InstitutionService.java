package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.PreOrderInstitution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.PreOrderInstitutionRepository;
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
    private final PreOrderInstitutionRepository preOrderInstitutionRepository;
    private final InstitutionUtils institutionUtils;
    @Override
    public InstitutionResponse createInstitution(InstitutionRequest institutionRequest) {
        Optional <Institution> institution = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equals(institutionRequest.getBececode())).findFirst();
        Institution institution1=new Institution();
        if (institution.isEmpty()) {
             institution1 = institutionUtils.mapInstitutionRequest_ToInstitution(institutionRequest);
            institutionRepository.save(institution1);

            InstitutionUtils.institutionGlobalList.add(institution1);
            log.info("Institution {} Saved Successfully", institution1.getIdInstitution());
        } else {
           Institution institution2=institutionUtils.mapInstitutionRequestToInstitution(institution.get(), institutionRequest);
            institutionRepository.save(institution2);
            InstitutionUtils.institutionGlobalList.add(institution2);
            return institutionUtils.mapInstitutionToInstitutionResponse(institution2);
        }
        return institutionUtils.mapInstitutionToInstitutionResponse(institution1);
    }
    @Override
    public InstitutionResponse migratePreOrder(String institutionCode) {
        Optional <PreOrderInstitution> institution = InstitutionUtils.preOrderInstitutionGlobalList.stream().filter(x -> x.getBececode().equals(institutionCode)).findFirst();
        Institution institution1=new Institution();
        if (institution.isPresent()) {
            Institution institution2 = institutionUtils.mapPreorderInstitutionToInstitution(institution.get());
            institutionRepository.save(institution2);
            InstitutionUtils.institutionGlobalList.add(institution2);
            return institutionUtils.mapInstitutionToInstitutionResponse(institution2);
        }
        return institutionUtils.mapInstitutionToInstitutionResponse(institution1);
    }
    @Override
    public String createPreOrderInstitution(PreOrderInstitutionRequest institutionRequest) {
       // Optional <PreOrderInstitution> institution = institutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equals(institutionRequest.getBececode())).findFirst();
        PreOrderInstitution institution1=new PreOrderInstitution();
            institution1 = institutionUtils.mapPreOrderInstitutionRequest_ToPreOrderInstitution(institutionRequest);
            preOrderInstitutionRepository.save(institution1);
            institutionUtils.createKeycloakCredentials(institution1);
            InstitutionUtils.preOrderInstitutionGlobalList.add(institution1);
            log.info("Institution {} Saved Successfully", institution1.getIdInstitution());

        return "Request Processed";
    }

    @Override
    public Optional<InstitutionResponse> getInstitutionByBeceCode(SingleStringRequest beceCode) {
        String finalBeceCode= beceCode.getVal();
        List<Institution> ii= InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equals(finalBeceCode)).toList();
        if(ii.size()==0){
            migratePreOrder(finalBeceCode);
        }
         return Optional.ofNullable(institutionUtils.mapInstitutionToInstitutionResponse(ii.get(0)));
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        return Optional.of(InstitutionUtils.institutionGlobalList.stream().filter(i->i.getAdmissions()!=null && i.getGradingSetting()!=null && i.getClassList()!=null && i.getSubjectList()!=null && i.getDepartmentList()!=null).map(institutionUtils::mapInstitutionToInstitutionResponse).toList());
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

    @Override
    public Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution() {
        return Optional.of(InstitutionUtils.preOrderInstitutionGlobalList.stream().map(institutionUtils::mapPreOrderInstitutionToPreOrderInstitutionResponse).toList());
    }

}

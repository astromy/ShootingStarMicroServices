package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionAccountRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.PreOrderInstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.PreOrderInstitutionResponse;
import com.astromyllc.shootingstar.setup.dto.response.SkimpInstitutionResponse;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.InstitutionAccount;
import com.astromyllc.shootingstar.setup.model.PreOrderInstitution;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.PreOrderInstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.InstitutionServiceInterface;
import com.astromyllc.shootingstar.setup.utils.InstitutionAccountUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InstitutionService implements InstitutionServiceInterface {
    private final InstitutionRepository institutionRepository;
    private final PreOrderInstitutionRepository preOrderInstitutionRepository;
    private final InstitutionUtils institutionUtils;
    private final InstitutionAccountUtil institutionAccountUtil;

    @Override
    public InstitutionResponse createInstitution(InstitutionRequest institutionRequest) {
        Optional<Institution> institution = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(institutionRequest.getBececode())).findFirst();
        Institution institution1 = new Institution();
        if (institution.isEmpty()) {
            institution1 = institutionUtils.mapInstitutionRequest_ToInstitution(institutionRequest);
            institutionRepository.save(institution1);

            InstitutionUtils.institutionGlobalList.add(institution1);
            log.info("Institution {} Saved Successfully", institution1.getIdInstitution());
        } else {
            Institution institution2 = institutionUtils.mapInstitutionRequestToInstitution(institution.get(), institutionRequest);
            institutionRepository.save(institution2);
            InstitutionUtils.institutionGlobalList.add(institution2);
            return institutionUtils.mapInstitutionToInstitutionResponse(institution2);
        }
        return institutionUtils.mapInstitutionToInstitutionResponse(institution1);
    }

    @Override
    public InstitutionResponse migratePreOrder(String institutionCode) {
        Optional<PreOrderInstitution> institution = InstitutionUtils.preOrderInstitutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(institutionCode)).findFirst();
        Institution institution1 = new Institution();
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
        // Optional <PreOrderInstitution> institution = institutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(institutionRequest.getBececode())).findFirst();
        PreOrderInstitution institution1 = new PreOrderInstitution();
        institution1 = institutionUtils.mapPreOrderInstitutionRequest_ToPreOrderInstitution(institutionRequest);
        preOrderInstitutionRepository.save(institution1);
        institutionUtils.createKeycloakCredentials(institution1);
        InstitutionUtils.preOrderInstitutionGlobalList.add(institution1);
        log.info("Institution {} Saved Successfully", institution1.getIdInstitution());

        return "Request Processed";
    }

    @Override
    public Optional<InstitutionResponse> getInstitutionByBeceCode(SingleStringRequest beceCode) {
        String finalBeceCode = beceCode.getVal();
        List<Institution> ii = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).toList();
        if (ii.size() == 0) {
            migratePreOrder(finalBeceCode);
            return Optional.ofNullable(institutionUtils.mapInstitutionToInstitutionResponse(InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).findFirst().get()));
        }
        return Optional.ofNullable(institutionUtils.mapInstitutionToInstitutionResponse(ii.get(0)));
    }

    @Override
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        return Optional.of(InstitutionUtils.institutionGlobalList.stream().filter(i -> i.getGradingSetting() != null && i.getClassList() != null && i.getSubjectList() != null && i.getDepartmentList() != null).map(institutionUtils::mapInstitutionToInstitutionResponse).toList());
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

    @Override
    public Optional<SkimpInstitutionResponse> getInstitutionStatus(SingleStringRequest beceCode) {
        String finalBeceCode = beceCode.getVal();
        List<Institution> ii = InstitutionUtils.institutionGlobalList.stream().filter(x -> x.getBececode().equalsIgnoreCase(finalBeceCode)).toList();

        return Optional.ofNullable(institutionUtils.mapInstitutionToSkimpInstitutionResponse(ii.get(0)));
    }

    @Override
    public Optional<String> reactivateInstitutionalAccount(PaystackPaymentResponse paystack) {
        String paymentStatus = paystack.getData().getStatus();
        Double paymentAmount = paystack.getData().getAmount();
        String institutionCode = paystack.getData().getMetadata().getCustomFields().get(0).getValue();
        Institution institution = InstitutionUtils.institutionGlobalList.stream().filter(inst -> inst.getBececode().equalsIgnoreCase(institutionCode)).findFirst().get();
        SkimpInstitutionResponse si = institutionUtils.mapInstitutionToSkimpInstitutionResponse(institution);

        if (paymentStatus.equalsIgnoreCase("success") && Objects.equals(paymentAmount, si.getPendingBill())) {
            String status = "active";
            List<InstitutionAccount> sa = new ArrayList<>();
            sa.add(InstitutionAccountUtil.mapInstitutionAccountRequest_ToInstitutionAccount(new InstitutionAccountRequest(institutionCode, status), institutionCode));
            institutionAccountUtil.saveAll(sa);
            institution.setStatus("active");
            institutionRepository.save(institution);
            return Optional.of("Account Reactivated for " + institutionCode);
        }
        return Optional.empty();
    }

}

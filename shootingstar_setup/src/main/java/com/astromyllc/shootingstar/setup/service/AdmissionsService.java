package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.AdmissionsEntryRequest;
import com.astromyllc.shootingstar.setup.dto.request.AdmissionsRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.AdmissionsResponse;
import com.astromyllc.shootingstar.setup.model.Admissions;
import com.astromyllc.shootingstar.setup.serviceInterface.AdmissionsServiceInterface;
import com.astromyllc.shootingstar.setup.utils.AdmissionUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdmissionsService implements AdmissionsServiceInterface {
    private final AdmissionUtil admissionUtil;

    @Override
    public Optional<AdmissionsResponse> createAdmissionSetup(AdmissionsEntryRequest admissionsRequest) {
        Admissions admissions=AdmissionUtil.mapAdmissionRequestToAdmission(admissionsRequest.getAdmissionList());
        InstitutionUtils.institutionGlobalList.stream().filter(i -> i.getBececode().equals(admissionsRequest.getInstitutioin())).findFirst().get().setAdmissions(admissions);
        return AdmissionUtil.mapAdmissionToAdmissionResponse(admissions);
    }

    @Override
    public Optional<AdmissionsResponse> createClasses(List<AdmissionsEntryRequest> admissionsRequestList) {

        return null;
    }

    @Override
    public Optional<List<AdmissionsResponse>> getAllAdmissionSetup(String beceCode) {
        return Optional.empty();
    }

    @Override
    public Optional<List<AdmissionsResponse>> getAllAdmissionSetupByClassGroup(String beceCode, String classGroup) {
        return Optional.empty();
    }

    @Override
    public Optional<AdmissionsResponse> getAllAdmissionSetupByInstitution(SingleStringRequest beceCode) {
        return AdmissionUtil.mapAdmissionToAdmissionResponse(InstitutionUtils.institutionGlobalList.stream().filter(i -> i.getBececode().equals(beceCode.getVal())).findFirst().get().getAdmissions());
    }
}

package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.AdmissionsEntryRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.AdmissionsResponse;
import com.astromyllc.shootingstar.setup.model.AdmissionCriteria;
import com.astromyllc.shootingstar.setup.model.Admissions;
import com.astromyllc.shootingstar.setup.model.ApplicationCategory;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.AdmissionsRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
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
    private final AdmissionsRepository admissionsRepository;
    private final InstitutionRepository institutionRepository;

    @Override
    public Optional<AdmissionsResponse> createAdmissionSetup(AdmissionsEntryRequest admissionsRequest) {
        //admissionsRepository.save(admissions);
        Institution inst= InstitutionUtils.institutionGlobalList.stream().filter(i -> i.getBececode().equalsIgnoreCase(admissionsRequest.getInstitution())).findFirst().get();

        // Get existiting criteria and categories and add to them
        Admissions existingAdmission=inst.getAdmissions();
        if(existingAdmission!=null) {
            if (admissionsRequest.getAdmissionList().getAdmissionCriteriaList().size() > 0) {
                inst.getAdmissions().getAdmissionCriteriaList().addAll(admissionsRequest.getAdmissionList().getAdmissionCriteriaList().stream().map(AdmissionUtil::mapCriteriaRequestToCriteria).toList());
            }
            if (admissionsRequest.getAdmissionList().getApplicationCategoryList().size() > 0) {
                inst.getAdmissions().getApplicationCategoryList().addAll(admissionsRequest.getAdmissionList().getApplicationCategoryList().stream().map(AdmissionUtil::mapCategoryRequestToCategory).toList());
            }
        }else {
            Admissions admissions=AdmissionUtil.mapAdmissionRequestToAdmission(admissionsRequest.getAdmissionList());
            inst.setAdmissions(admissions);
        }
        institutionRepository.save(inst);

        return AdmissionUtil.mapAdmissionToAdmissionResponse(inst.getAdmissions());
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
        Optional<AdmissionsResponse> admissionsResponse= AdmissionUtil.mapAdmissionToAdmissionResponse(
                InstitutionUtils.institutionGlobalList.stream()
                        .filter(i -> i.getBececode().equalsIgnoreCase(beceCode.getVal()))
                        .findFirst().get()
                        .getAdmissions());
        return admissionsResponse;
    }
}

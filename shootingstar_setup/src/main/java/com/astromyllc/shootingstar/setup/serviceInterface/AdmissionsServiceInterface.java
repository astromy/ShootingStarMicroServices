package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.AdmissionsEntryRequest;
import com.astromyllc.shootingstar.setup.dto.request.AdmissionsRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.AdmissionsResponse;

import java.util.List;
import java.util.Optional;

public interface AdmissionsServiceInterface {
    public Optional<AdmissionsResponse> createAdmissionSetup(AdmissionsEntryRequest admissionsRequest);
    public  Optional<AdmissionsResponse> createClasses(List<AdmissionsEntryRequest> admissionsRequestList);
    public Optional<List<AdmissionsResponse>> getAllAdmissionSetup(String beceCode);
    public Optional<List<AdmissionsResponse>> getAllAdmissionSetupByClassGroup(String beceCode,String classGroup);
    public Optional<AdmissionsResponse> getAllAdmissionSetupByInstitution(SingleStringRequest beceCode );
}

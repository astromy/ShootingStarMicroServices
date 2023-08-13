package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.AdmissionsRequest;
import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;

import java.util.List;
import java.util.Optional;

public interface AdmissionsServiceInterface {
    public void createAdmissionSetup(AdmissionsRequest admissionsRequest);
    public  void createClasses(List<AdmissionsRequest> admissionsRequestList);
    public Optional<List<AdmissionsRequest>> getAllAdmissionSetup(String beceCode);
    public Optional<List<AdmissionsRequest>> getAllAdmissionSetupByClassGroup(String beceCode,String classGroup);
    public Optional<List<AdmissionsRequest>> getAllAdmissionSetupByInstitution(String beceCode );
}

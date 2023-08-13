package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.AdmissionsRequest;
import com.astromyllc.shootingstar.setup.serviceInterface.AdmissionsServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdmissionsService implements AdmissionsServiceInterface {
    @Override
    public void createAdmissionSetup(AdmissionsRequest admissionsRequest) {

    }

    @Override
    public void createClasses(List<AdmissionsRequest> admissionsRequestList) {

    }

    @Override
    public Optional<List<AdmissionsRequest>> getAllAdmissionSetup(String beceCode) {
        return Optional.empty();
    }

    @Override
    public Optional<List<AdmissionsRequest>> getAllAdmissionSetupByClassGroup(String beceCode, String classGroup) {
        return Optional.empty();
    }

    @Override
    public Optional<List<AdmissionsRequest>> getAllAdmissionSetupByInstitution(String beceCode) {
        return Optional.empty();
    }
}

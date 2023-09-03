package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VitalRecordsRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VitalRecordsResponse;
import com.astromyllc.shootingstar.clinic.repository.VitalRecordsRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.VitalRecordsServiceInterface;
import com.astromyllc.shootingstar.clinic.util.VitalRecordsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VitalRecordsService implements VitalRecordsServiceInterface {
    private final VitalRecordsRepository vitalRecordsRepository;
    private final VitalRecordsUtil vitalRecordsUtil;
    @Override
    public void recordVital(VitalRecordsRequest vitalRecordsRequest) {

    }

    @Override
    public void recordVitals(List<VitalRecordsRequest> vitalRecordsRequestList) {

    }

    @Override
    public VitalRecordsResponse fetchVitalRecordsByPatient(PatientRequest patientRequest) {
        return null;
    }

    @Override
    public List<VitalRecordsResponse> fetchVitalRecordsByInstitution(PatientRequest patientRequest) {
        return null;
    }
}

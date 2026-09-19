package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VitalRecordsRequest;
import com.astromyllc.shootingstar.clinic.dto.response.VitalRecordsResponse;
import com.astromyllc.shootingstar.clinic.model.VitalRecords;
import com.astromyllc.shootingstar.clinic.repository.VitalRecordsRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.VitalRecordsServiceInterface;
import com.astromyllc.shootingstar.clinic.util.VitalRecordsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
        VitalRecords vitalRecords = vitalRecordsUtil.mapVitalRecordsRequest_ToVitalRecords(vitalRecordsRequest);
        vitalRecordsRepository.save(vitalRecords);
        VitalRecordsUtil.vitalRecordsList.add(vitalRecords);
    }

    @Override
    public void recordVitals(List<VitalRecordsRequest> vitalRecordsRequestList) {
        List<VitalRecords> vitalRecords = vitalRecordsRequestList.stream()
                .map(vitalRecordsUtil::mapVitalRecordsRequest_ToVitalRecords).toList();
        vitalRecordsRepository.saveAll(vitalRecords);
        VitalRecordsUtil.vitalRecordsList.addAll(vitalRecords);
    }

    // Interface returns a single response (not a list) — matches the most
    // recent vital record for this patient, since that's what a "what are
    // this patient's vitals" screen actually wants to show.
    @Override
    public VitalRecordsResponse fetchVitalRecordsByPatient(PatientRequest patientRequest) {
        return VitalRecordsUtil.vitalRecordsList.stream()
                .filter(v -> v.getPatientId().equalsIgnoreCase(patientRequest.getPatientId())
                        && v.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode()))
                .max(Comparator.comparing(VitalRecords::getDateTime))
                .map(vitalRecordsUtil::mapVitalRecords_ToVitalRecordsResponse)
                .orElse(null);
    }

    @Override
    public List<VitalRecordsResponse> fetchVitalRecordsByInstitution(PatientRequest patientRequest) {
        return VitalRecordsUtil.vitalRecordsList.stream()
                .filter(v -> v.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode()))
                .map(vitalRecordsUtil::mapVitalRecords_ToVitalRecordsResponse).toList();
    }

    @Override
    public List<VitalRecordsResponse> fetchAllVitalRecordsByPatient(PatientRequest patientRequest) {
        return VitalRecordsUtil.vitalRecordsList.stream()
                .filter(v -> v.getPatientId().equalsIgnoreCase(patientRequest.getPatientId())
                        && v.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode()))
                .sorted(Comparator.comparing(VitalRecords::getDateTime).reversed())
                .map(vitalRecordsUtil::mapVitalRecords_ToVitalRecordsResponse).toList();
    }
}

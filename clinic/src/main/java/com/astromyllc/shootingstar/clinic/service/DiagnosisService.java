package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.dto.request.DiagnosisRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.response.DiagnosisResponse;
import com.astromyllc.shootingstar.clinic.model.Diagnosis;
import com.astromyllc.shootingstar.clinic.repository.DiagnosisRepository;
import com.astromyllc.shootingstar.clinic.serviceInterface.DiagnosisServiceInterface;
import com.astromyllc.shootingstar.clinic.util.DiagnosisUtil;
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
public class DiagnosisService implements DiagnosisServiceInterface {
    private final DiagnosisRepository diagnosisRepository;
    private final DiagnosisUtil diagnosisUtil;
    @Override
    public void recordDiagnosis(DiagnosisRequest diagnosisRequest) {
        Diagnosis diagnosis=diagnosisUtil.mapDiagnososRequest_ToDiagnosis(diagnosisRequest);
        diagnosisRepository.save(diagnosis);
        DiagnosisUtil.diagnosisGlobalList.add(diagnosis);
    }

    @Override
    public void recordDiagnosisList(List<DiagnosisRequest> diagnosisRequests) {
        List<Diagnosis> diagnosis=diagnosisRequests.stream().map(diagnosisUtil::mapDiagnososRequest_ToDiagnosis).toList();
        diagnosisRepository.saveAll(diagnosis);
        DiagnosisUtil.diagnosisGlobalList.addAll(diagnosis);
    }

    @Override
    public Optional<List<DiagnosisResponse>> fetchDiagnosisByPatient(PatientRequest patientRequest) {
        return Optional.of(DiagnosisUtil.diagnosisGlobalList.stream()
                .filter(diagnosis1 -> diagnosis1.getPatientId().equalsIgnoreCase(patientRequest.getPatientId())
                        && diagnosis1.getInstitutionCode().equalsIgnoreCase(patientRequest.getInstitutionCode())).toList()
                .stream().map(diagnosisUtil::mapDiagnosis_ToDiagnosisResponse).toList());
    }

    @Override
    public Optional<List<DiagnosisResponse>> fetchDiagnosisByInstitution(PatientRequest patientRequest) {
        return Optional.empty();
    }
}

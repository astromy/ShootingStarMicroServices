package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.dto.request.PrescriptionRequest;
import com.astromyllc.shootingstar.clinic.dto.response.PrescriptionResponse;
import com.astromyllc.shootingstar.clinic.model.Prescription;
import com.astromyllc.shootingstar.clinic.repository.PrescriptionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PrescriptionUtil {
    private final PrescriptionRepository prescriptionRepository;
    public static List<Prescription> prescriptionGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetchAllPrescriptions() {
        prescriptionGlobalList = prescriptionRepository.findAll();
        log.info("Global Prescription List populated with {} records", prescriptionGlobalList.size());
    }

    public Prescription mapPrescriptionRequest_ToPrescription(PrescriptionRequest prescriptionRequest) {
        return Prescription.builder()
                .dateTime(LocalDateTime.parse(prescriptionRequest.getDateTime(), formatter))
                .prescription(prescriptionRequest.getPrescription())
                .institutionCode(prescriptionRequest.getInstitutionCode())
                .patientId(prescriptionRequest.getPatientId())
                .patientType(prescriptionRequest.getPatientType())
                .build();
    }

    public PrescriptionResponse mapPrescription_ToPrescriptionResponse(Prescription prescription) {
        return PrescriptionResponse.builder()
                .id(prescription.getId())
                .dateTime(prescription.getDateTime())
                .prescription(prescription.getPrescription())
                .patientId(prescription.getPatientId())
                .patientType(prescription.getPatientType())
                .build();
    }
}

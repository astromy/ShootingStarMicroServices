package com.astromyllc.shootingstar.clinic.controller;

import com.astromyllc.shootingstar.clinic.dto.request.DiagnosisRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PatientRequest;
import com.astromyllc.shootingstar.clinic.dto.request.PrescriptionRequest;
import com.astromyllc.shootingstar.clinic.dto.request.VitalRecordsRequest;
import com.astromyllc.shootingstar.clinic.serviceInterface.DiagnosisServiceInterface;
import com.astromyllc.shootingstar.clinic.serviceInterface.PrescriptionServiceInterface;
import com.astromyllc.shootingstar.clinic.serviceInterface.VitalRecordsServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Standalone record-keeping endpoints for the three record types a visit can
 * link to (Diagnosis / Prescription / VitalRecords) — mainly for pulling up
 * a patient's clinic history outside the context of a single visit. Most of
 * the time these get created as part of VisitController#recordVisit instead.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClinicRecordsController {

    private final DiagnosisServiceInterface diagnosisServiceInterface;
    private final PrescriptionServiceInterface prescriptionServiceInterface;
    private final VitalRecordsServiceInterface vitalRecordsServiceInterface;

    // ── Diagnosis ───────────────────────────────────────────────────────
    @PostMapping("/api/clinic/recordDiagnosis")
    public ResponseEntity<Void> recordDiagnosis(@RequestBody DiagnosisRequest request) {
        diagnosisServiceInterface.recordDiagnosis(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/clinic/getDiagnosisByPatient")
    public ResponseEntity<?> getDiagnosisByPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(diagnosisServiceInterface.fetchDiagnosisByPatient(request));
    }

    // ── Prescription ────────────────────────────────────────────────────
    @PostMapping("/api/clinic/recordPrescription")
    public ResponseEntity<Void> recordPrescription(@RequestBody PrescriptionRequest request) {
        prescriptionServiceInterface.insertPrescription(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/clinic/getPrescriptionByPatient")
    public ResponseEntity<?> getPrescriptionByPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(prescriptionServiceInterface.fetchPrescriptionByPatient(request));
    }

    // ── Vital Records ───────────────────────────────────────────────────
    @PostMapping("/api/clinic/recordVital")
    public ResponseEntity<Void> recordVital(@RequestBody VitalRecordsRequest request) {
        vitalRecordsServiceInterface.recordVital(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/clinic/getVitalRecordsByPatient")
    public ResponseEntity<?> getVitalRecordsByPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(vitalRecordsServiceInterface.fetchVitalRecordsByPatient(request));
    }

    // Full timeline (getVitalRecordsByPatient above only returns the single
    // latest record) — what the Medical History view actually needs.
    @PostMapping("/api/clinic/getAllVitalRecordsByPatient")
    public ResponseEntity<?> getAllVitalRecordsByPatient(@RequestBody PatientRequest request) {
        return ResponseEntity.ok(vitalRecordsServiceInterface.fetchAllVitalRecordsByPatient(request));
    }
}
